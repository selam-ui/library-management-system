package service;

import dto.BookDTO;
import exception.ValidationException;
import java.util.List;

public interface BookService {
    List<BookDTO> listAll();
    List<BookDTO> listAvailable();
    List<BookDTO> listBorrowed();
    List<BookDTO> listNewest();
    BookDTO add(BookDTO dto) throws ValidationException;
    boolean update(BookDTO dto) throws ValidationException;
    boolean delete(int id) throws ValidationException;
    List<BookDTO> search(String keyword);
}
