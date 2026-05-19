package mapper;

import dto.BorrowRecordDTO;
import model.BorrowRecord;
import model.Book;

public class BorrowRecordMapper {
    public static BorrowRecordDTO toDTO(BorrowRecord record, Book book) {
        if (record == null) return null;
        return new BorrowRecordDTO(
                record.getId(),
                record.getBookId(),
                book != null ? book.getTitle() : "",
                book != null ? book.getIsbn() : "",
                record.getBorrowDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus());
    }
}
