package service.impl;

import dao.DBConnection;
import enumtypes.Role;
import exception.AuthenticationException;
import exception.DatabaseException;
import exception.ValidationException;
import model.User;
import service.AuthService;
import session.SessionManager;
import util.PasswordUtil;
import validator.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    @Override
    public User login(String username, String password) throws AuthenticationException, ValidationException {
        UserValidator.validateLogin(username, password);
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new AuthenticationException("Invalid credentials");
                String stored = rs.getString("password");
                boolean ok = stored.startsWith("$2a$") || stored.startsWith("$2b$")
                        ? PasswordUtil.verify(password, stored)
                        : stored.equals(password); // fallback for plain seed data
                if (!ok) throw new AuthenticationException("Invalid credentials");

                User u = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        stored,
                        rs.getString("full_name"),
                        rs.getString("email"),
                        Role.valueOf(rs.getString("role")));
                SessionManager.login(u);
                return u;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Login query failed", e);
        }
    }

    @Override
    public void logout() { SessionManager.logout(); }
}
