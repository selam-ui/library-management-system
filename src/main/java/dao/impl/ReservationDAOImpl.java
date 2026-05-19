package dao.impl;

import dao.DBConnection;
import dao.interfaces.ReservationDAO;
import exception.DatabaseException;
import model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {
    private Reservation map(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getTimestamp("reserved_at").toLocalDateTime(),
                rs.getBoolean("notified"),
                rs.getBoolean("fulfilled")
        );
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations(user_id, book_id, reserved_at, notified, fulfilled) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getBookId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getReservedAt()));
            ps.setBoolean(4, reservation.isNotified());
            ps.setBoolean(5, reservation.isFulfilled());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) reservation.setId(keys.getInt(1));
            }
            return reservation;
        } catch (SQLException e) {
            throw new DatabaseException("save reservation failed", e);
        }
    }

    @Override
    public List<Reservation> findByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY reserved_at DESC";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) reservations.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find reservations failed", e);
        }
        return reservations;
    }

    @Override
    public Optional<Reservation> findByUserIdAndBookId(int userId, int bookId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND book_id = ? AND fulfilled = FALSE";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find reservation failed", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Reservation> findNextPendingByBook(int bookId) {
        String sql = "SELECT * FROM reservations WHERE book_id = ? AND notified = FALSE AND fulfilled = FALSE ORDER BY reserved_at ASC LIMIT 1";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find next pending reservation failed", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Reservation> findReadyByUserAndBook(int userId, int bookId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND book_id = ? AND notified = TRUE AND fulfilled = FALSE";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find ready reservation failed", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findReadyByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND notified = TRUE AND fulfilled = FALSE ORDER BY reserved_at DESC";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) reservations.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("find ready reservations failed", e);
        }
        return reservations;
    }

    @Override
    public boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET user_id=?, book_id=?, reserved_at=?, notified=?, fulfilled=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getBookId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getReservedAt()));
            ps.setBoolean(4, reservation.isNotified());
            ps.setBoolean(5, reservation.isFulfilled());
            ps.setInt(6, reservation.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("update reservation failed", e);
        }
    }
}
