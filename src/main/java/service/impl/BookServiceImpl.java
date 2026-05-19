package service.impl;

import dao.impl.BookDAOImpl;
import dao.interfaces.BookDAO;
import dto.BookDTO;
import enumtypes.Role;
import exception.ValidationException;
import mapper.BookMapper;
import model.Book;
import model.User;
import service.BookService;
import session.SessionManager;
import util.Validator;

import java.util.List;

public class BookServiceImpl implements BookService {
    private final BookDAO dao = new BookDAOImpl();

    private void ensureBookAccess() throws ValidationException {
        User user = SessionManager.getCurrentUser();
        if (user == null) throw new ValidationException("Please log in before managing books.");
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.LIBRARIAN) {
            throw new ValidationException("Only admins and librarians can manage books.");
        }
    }

    @Override
    public List<BookDTO> listAll() {
        return dao.findAll().stream().map(BookMapper::toDTO).toList();
    }

    @Override
    public List<BookDTO> listAvailable() {
        return dao.findAvailable().stream().map(BookMapper::toDTO).toList();
    }

    @Override
    public List<BookDTO> listBorrowed() {
        return dao.findBorrowed().stream().map(BookMapper::toDTO).toList();
    }

    @Override
    public List<BookDTO> listNewest() {
        return dao.findNewest().stream().map(BookMapper::toDTO).toList();
    }

    private void validate(BookDTO d) throws ValidationException {
        if (d == null) throw new ValidationException("Book is null");
        if (Validator.isBlank(d.getTitle())) throw new ValidationException("Title is required");
        if (Validator.isBlank(d.getAuthor())) throw new ValidationException("Author is required");
        if (d.getTotalCopies() < 0) throw new ValidationException("Total copies cannot be negative");
        if (d.getAvailableCopies() < 0 || d.getAvailableCopies() > d.getTotalCopies())
            throw new ValidationException("Invalid available copies");
    }

    @Override
    public BookDTO add(BookDTO dto) throws ValidationException {
        ensureBookAccess();
        validate(dto);
        Book saved = dao.save(BookMapper.toEntity(dto));
        return BookMapper.toDTO(saved);
    }

    @Override
    public boolean update(BookDTO dto) throws ValidationException {
        ensureBookAccess();
        validate(dto);
        return dao.update(BookMapper.toEntity(dto));
    }

    @Override
    public boolean delete(int id) throws ValidationException {
        ensureBookAccess();
        return dao.delete(id);
    }

    @Override
    public List<BookDTO> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return listAll();
        return dao.search(keyword).stream().map(BookMapper::toDTO).toList();
    }
}
