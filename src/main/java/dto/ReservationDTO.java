package dto;

import java.time.LocalDateTime;

public class ReservationDTO {
    private int id;
    private int bookId;
    private String bookTitle;
    private String isbn;
    private String status;
    private LocalDateTime reservedAt;

    public ReservationDTO() {}

    public ReservationDTO(int id, int bookId, String bookTitle, String isbn, String status, LocalDateTime reservedAt) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }

    @Override
    public String toString() {
        return bookTitle + " (" + status + ")";
    }
}
