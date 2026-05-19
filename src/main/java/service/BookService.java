package service;

import dto.BookDTO;
import exception.ValidationException;
import java.util.List;

public interface BookService {
    List<BookDTO> listAll();
    BookDTO add(BookDTO dto) throws ValidationException;
    boolean update(BookDTO dto) throws ValidationException;
    boolean delete(int id);
    List<BookDTO> search(String keyword);
}
