package dao.interfaces;

import model.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    Reservation save(Reservation reservation);
    List<Reservation> findByUserId(int userId);
    Optional<Reservation> findByUserIdAndBookId(int userId, int bookId);
    Optional<Reservation> findNextPendingByBook(int bookId);
    Optional<Reservation> findReadyByUserAndBook(int userId, int bookId);
    List<Reservation> findReadyByUserId(int userId);
    boolean update(Reservation reservation);
}
