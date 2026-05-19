package dao.impl;

import dao.DBConnection;
import dao.interfaces.UserDAO;
import exception.DatabaseException;
import model.User;
import enumtypes.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                Role.from(rs.getString("role")),
                rs.getBoolean("approved"),
                rs.getBoolean("active"));
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, username";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("findAll users failed", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("findById user failed", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("findByUsername failed", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users(username, password, full_name, email, role, approved, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isApproved());
            ps.setBoolean(7, user.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("save user failed", e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username=?, password=?, full_name=?, email=?, role=?, approved=?, active=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isApproved());
            ps.setBoolean(7, user.isActive());
            ps.setInt(8, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("update user failed", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("delete user failed", e);
        }
    }
}
