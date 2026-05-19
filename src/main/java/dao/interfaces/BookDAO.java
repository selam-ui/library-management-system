package dao.interfaces;

import model.Book;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    List<Book> findAll();
    Optional<Book> findById(int id);
    Book save(Book book);
    boolean update(Book book);
    boolean delete(int id);
    List<Book> search(String keyword);
    List<Book> findAvailable();
    List<Book> findBorrowed();
    List<Book> findNewest();
}
