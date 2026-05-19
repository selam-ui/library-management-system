package dao.impl;

import dao.DBConnection;
import dao.interfaces.BorrowRecordDAO;
import exception.DatabaseException;
import model.BorrowRecord;
import enumtypes.BorrowStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowRecordDAOImpl implements BorrowRecordDAO {
    private BorrowRecord map(ResultSet rs) throws SQLException {
        return new BorrowRecord(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getDate("borrow_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                BorrowStatus.valueOf(rs.getString("status")));
    }

    @Override
    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records ORDER BY borrow_date DESC";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) records.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("findAll borrow records failed", e);
        }
        return records;
    }

    @Override
    public List<BorrowRecord> findByUserId(int userId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ? ORDER BY borrow_date DESC";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("find borrow records failed", e);
        }
        return records;
    }

    @Override
    public Optional<BorrowRecord> findById(int id) {
        String sql = "SELECT * FROM borrow_records WHERE id = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find borrow record failed", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BorrowRecord> findActiveByBookAndUser(int bookId, int userId) {
        String sql = "SELECT * FROM borrow_records WHERE book_id = ? AND user_id = ? AND status = 'BORROWED'";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find active borrow failed", e);
        }
        return Optional.empty();
    }

    @Override
    public void markOverdueRecords() {
        String sql = "UPDATE borrow_records SET status = 'OVERDUE' WHERE due_date < CURDATE() AND return_date IS NULL AND status = 'BORROWED'";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("mark overdue records failed", e);
        }
    }

    @Override
    public BorrowRecord save(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records(user_id, book_id, borrow_date, due_date, return_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getUserId());
            ps.setInt(2, record.getBookId());
            ps.setDate(3, Date.valueOf(record.getBorrowDate()));
            ps.setDate(4, Date.valueOf(record.getDueDate()));
            ps.setDate(5, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            ps.setString(6, record.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) record.setId(keys.getInt(1));
            }
            return record;
        } catch (SQLException e) {
            throw new DatabaseException("save borrow record failed", e);
        }
    }

    @Override
    public boolean update(BorrowRecord record) {
        String sql = "UPDATE borrow_records SET user_id=?, book_id=?, borrow_date=?, due_date=?, return_date=?, status=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, record.getUserId());
            ps.setInt(2, record.getBookId());
            ps.setDate(3, Date.valueOf(record.getBorrowDate()));
            ps.setDate(4, Date.valueOf(record.getDueDate()));
            ps.setDate(5, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            ps.setString(6, record.getStatus().name());
            ps.setInt(7, record.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("update borrow record failed", e);
        }
    }
}
