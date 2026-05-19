package service.impl;

import dao.impl.BookDAOImpl;
import dao.impl.BorrowRecordDAOImpl;
import dao.impl.ReservationDAOImpl;
import dao.interfaces.BookDAO;
import dao.interfaces.BorrowRecordDAO;
import dao.interfaces.ReservationDAO;
import dto.BorrowRecordDTO;
import enumtypes.Role;
import exception.ValidationException;
import mapper.BorrowRecordMapper;
import model.Book;
import model.BorrowRecord;
import model.Reservation;
import service.BorrowService;
import session.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class BorrowServiceImpl implements BorrowService {
    private final BorrowRecordDAO borrowDao = new BorrowRecordDAOImpl();
    private final BookDAO bookDao = new BookDAOImpl();
    private final ReservationDAO reservationDao = new ReservationDAOImpl();

    private void syncOverdueRecords() {
        borrowDao.markOverdueRecords();
    }

    @Override
    public List<BorrowRecordDTO> listBorrowRecords() {
        var user = SessionManager.getCurrentUser();
        if (user == null) throw new IllegalStateException("User must be logged in to view borrowed books");
        syncOverdueRecords();
        List<BorrowRecord> records;
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.LIBRARIAN) {
            records = borrowDao.findAll();
        } else {
            records = borrowDao.findByUserId(user.getId());
        }
        return records.stream()
                .map(record -> BorrowRecordMapper.toDTO(record, bookDao.findById(record.getBookId()).orElse(null)))
                .toList();
    }

    @Override
    public BorrowRecordDTO borrowBook(int bookId) throws ValidationException {
        var user = SessionManager.getCurrentUser();
        if (user == null) throw new ValidationException("Please log in before borrowing books.");
        var bookOpt = bookDao.findById(bookId);
        if (bookOpt.isEmpty()) throw new ValidationException("Selected book was not found.");
        Book book = bookOpt.get();
        var readyReservation = reservationDao.findReadyByUserAndBook(user.getId(), bookId);
        if (readyReservation.isEmpty() && book.getAvailableCopies() <= 0) {
            throw new ValidationException("No copies available for this book.");
        }
        if (borrowDao.findActiveByBookAndUser(bookId, user.getId()).isPresent()) {
            throw new ValidationException("You already borrowed this book and have not returned it yet.");
        }

        if (readyReservation.isEmpty()) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            if (!bookDao.update(book)) throw new ValidationException("Unable to update book availability.");
        } else {
            Reservation ready = readyReservation.get();
            ready.setFulfilled(true);
            if (!reservationDao.update(ready)) {
                throw new ValidationException("Unable to mark reservation as fulfilled.");
            }
        }

        BorrowRecord record = new BorrowRecord();
        record.setUserId(user.getId());
        record.setBookId(bookId);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setStatus(enumtypes.BorrowStatus.BORROWED);
        BorrowRecord saved = borrowDao.save(record);
        return BorrowRecordMapper.toDTO(saved, book);
    }

    @Override
    public BorrowRecordDTO returnBook(int borrowRecordId) throws ValidationException {
        var user = SessionManager.getCurrentUser();
        if (user == null) throw new ValidationException("Please log in before returning books.");
        var recordOpt = borrowDao.findById(borrowRecordId);
        if (recordOpt.isEmpty()) throw new ValidationException("Borrow record not found.");
        BorrowRecord record = recordOpt.get();
        if (record.getUserId() != user.getId() && user.getRole() == Role.STUDENT) {
            throw new ValidationException("You can only return your own borrowed books.");
        }
        if (record.getStatus() == enumtypes.BorrowStatus.RETURNED) {
            throw new ValidationException("This book is already returned.");
        }

        var bookOpt = bookDao.findById(record.getBookId());
        if (bookOpt.isEmpty()) throw new ValidationException("Book record not found.");
        Book book = bookOpt.get();

        record.setReturnDate(LocalDate.now());
        record.setStatus(enumtypes.BorrowStatus.RETURNED);
        if (!borrowDao.update(record)) throw new ValidationException("Unable to update borrow record.");

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (!bookDao.update(book)) throw new ValidationException("Unable to update book availability after return.");
        var pendingRes = reservationDao.findNextPendingByBook(book.getId());
        if (pendingRes.isPresent()) {
            Reservation reservation = pendingRes.get();
            reservation.setNotified(true);
            if (!reservationDao.update(reservation)) {
                throw new ValidationException("Unable to update reservation notification.");
            }
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            if (!bookDao.update(book)) {
                throw new ValidationException("Unable to reserve returned copy for reservation.");
            }
        }

        return BorrowRecordMapper.toDTO(record, book);
    }
}
