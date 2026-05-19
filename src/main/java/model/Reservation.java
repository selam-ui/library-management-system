package model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int userId;
    private int bookId;
    private LocalDateTime reservedAt;
    private boolean notified;
    private boolean fulfilled;

    public Reservation() {}

    public Reservation(int id, int userId, int bookId, LocalDateTime reservedAt, boolean notified, boolean fulfilled) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.reservedAt = reservedAt;
        this.notified = notified;
        this.fulfilled = fulfilled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }
    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
    public boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }
}
