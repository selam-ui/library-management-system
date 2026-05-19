package dao.interfaces;

import model.BorrowRecord;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordDAO {
    List<BorrowRecord> findAll();
    List<BorrowRecord> findByUserId(int userId);
    Optional<BorrowRecord> findById(int id);
    Optional<BorrowRecord> findActiveByBookAndUser(int bookId, int userId);
    BorrowRecord save(BorrowRecord record);
    boolean update(BorrowRecord record);
    void markOverdueRecords();
}
