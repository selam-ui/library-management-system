package service.impl;

import dao.impl.BookDAOImpl;
import dao.interfaces.BookDAO;
import dto.BookDTO;
import exception.ValidationException;
import mapper.BookMapper;
import model.Book;
import service.BookService;
import util.Validator;

import java.util.List;

public class BookServiceImpl implements BookService {
    private final BookDAO dao = new BookDAOImpl();

    @Override
    public List<BookDTO> listAll() {
        return dao.findAll().stream().map(BookMapper::toDTO).toList();
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
        validate(dto);
        Book saved = dao.save(BookMapper.toEntity(dto));
        return BookMapper.toDTO(saved);
    }

    @Override
    public boolean update(BookDTO dto) throws ValidationException {
        validate(dto);
        return dao.update(BookMapper.toEntity(dto));
    }

    @Override
    public boolean delete(int id) { return dao.delete(id); }

    @Override
    public List<BookDTO> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return listAll();
        return dao.search(keyword).stream().map(BookMapper::toDTO).toList();
    }
}
