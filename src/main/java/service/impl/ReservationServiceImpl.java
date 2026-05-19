package service.impl;

import dao.impl.BookDAOImpl;
import dao.impl.ReservationDAOImpl;
import dao.interfaces.BookDAO;
import dao.interfaces.ReservationDAO;
import dto.ReservationDTO;
import enumtypes.ReservationStatus;
import exception.ValidationException;
import mapper.ReservationMapper;
import model.Book;
import model.Reservation;
import session.SessionManager;
import util.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationServiceImpl implements service.ReservationService {
    private final ReservationDAO reservationDao = new ReservationDAOImpl();
    private final BookDAO bookDao = new BookDAOImpl();

    private void ensureLoggedIn() throws ValidationException {
        if (SessionManager.getCurrentUser() == null) {
            throw new ValidationException("Please log in before reserving books.");
        }
    }

    @Override
    public List<ReservationDTO> listMyReservations() throws ValidationException {
        ensureLoggedIn();
        var user = SessionManager.getCurrentUser();
        return reservationDao.findByUserId(user.getId()).stream()
                .map(r -> {
                    var book = bookDao.findById(r.getBookId()).orElse(null);
                    return ReservationMapper.toDTO(r,
                            book != null ? book.getTitle() : "Unknown",
                            book != null ? book.getIsbn() : "");
                }).toList();
    }

    @Override
    public ReservationDTO reserveBook(int bookId) throws ValidationException {
        ensureLoggedIn();
        var user = SessionManager.getCurrentUser();
        var bookOpt = bookDao.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new ValidationException("Selected book was not found.");
        }
        Book book = bookOpt.get();
        if (book.getAvailableCopies() > 0) {
            throw new ValidationException("This book is currently available. Borrow it instead of reserving it.");
        }
        if (reservationDao.findByUserIdAndBookId(user.getId(), bookId).isPresent()) {
            throw new ValidationException("You already have a reservation or ready notification for this book.");
        }
        if (Validator.isBlank(book.getTitle())) {
            throw new ValidationException("Book information is incomplete.");
        }
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        reservation.setBookId(bookId);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotified(false);
        reservation.setFulfilled(false);
        Reservation saved = reservationDao.save(reservation);
        return ReservationMapper.toDTO(saved, book.getTitle(), book.getIsbn());
    }

    @Override
    public int countReadyNotifications() throws ValidationException {
        ensureLoggedIn();
        var user = SessionManager.getCurrentUser();
        return reservationDao.findReadyByUserId(user.getId()).size();
    }

    public void markNextReservationReady(int bookId) throws ValidationException {
        var reservationOpt = reservationDao.findNextPendingByBook(bookId);
        if (reservationOpt.isEmpty()) return;
        Reservation reservation = reservationOpt.get();
        reservation.setNotified(true);
        if (!reservationDao.update(reservation)) {
            throw new ValidationException("Unable to update reservation notification.");
        }
    }
}
