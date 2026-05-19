package mapper;

import dto.BookDTO;
import model.Book;

public class BookMapper {
    public static BookDTO toDTO(Book b) {
        if (b == null) return null;
        return new BookDTO(b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getCategory(), b.getTotalCopies(), b.getAvailableCopies());
    }
    public static Book toEntity(BookDTO d) {
        if (d == null) return null;
        return new Book(d.getId(), d.getIsbn(), d.getTitle(), d.getAuthor(),
                d.getCategory(), d.getTotalCopies(), d.getAvailableCopies());
    }
}
