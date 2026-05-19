package dto;

import enumtypes.BorrowStatus;
import java.time.LocalDate;

public class BorrowRecordDTO {
    private int id;
    private int bookId;
    private String bookTitle;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;

    public BorrowRecordDTO() {}

    public BorrowRecordDTO(int id, int bookId, String bookTitle, String isbn,
                           LocalDate borrowDate, LocalDate dueDate,
                           LocalDate returnDate, BorrowStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }
}
