package dao.impl;

import dao.DBConnection;
import dao.interfaces.BookDAO;
import exception.DatabaseException;
import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"));
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("findAll books failed", e);
        }
        return books;
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("findById failed", e);
        }
        return Optional.empty();
    }

    @Override
    public Book save(Book b) {
        String sql = "INSERT INTO books(isbn,title,author,category,total_copies,available_copies) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getCategory());
            ps.setInt(5, b.getTotalCopies());
            ps.setInt(6, b.getAvailableCopies());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) b.setId(keys.getInt(1));
            }
            return b;
        } catch (SQLException e) {
            throw new DatabaseException("save book failed", e);
        }
    }

    @Override
    public boolean update(Book b) {
        String sql = "UPDATE books SET isbn=?,title=?,author=?,category=?,total_copies=?,available_copies=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getCategory());
            ps.setInt(5, b.getTotalCopies());
            ps.setInt(6, b.getAvailableCopies());
            ps.setInt(7, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("update book failed", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("delete book failed", e);
        }
    }

    @Override
    public List<Book> search(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("search failed", e);
        }
        return books;
    }

    @Override
    public List<Book> findAvailable() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_copies > 0 ORDER BY title";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("find available books failed", e);
        }
        return books;
    }

    @Override
    public List<Book> findBorrowed() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_copies < total_copies ORDER BY title";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("find borrowed books failed", e);
        }
        return books;
    }

    @Override
    public List<Book> findNewest() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) books.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("find newest books failed", e);
        }
        return books;
    }
}
