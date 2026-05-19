package util;

import enumtypes.Role;
import exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void ensureDefaultUsers(Connection connection) {
        try {
            normalizeLegacyRoles(connection);
            ensureUserStateColumns(connection);
            upsertDefaultUser(connection, "admin", "admin123", "Administrator", "admin@lib.local", Role.ADMIN);
            upsertDefaultUser(connection, "librarian", "admin123", "Library Staff", "librarian@lib.local", Role.LIBRARIAN);
            upsertDefaultUser(connection, "student", "member123", "Default Student", "student@lib.local", Role.STUDENT);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to ensure default users", e);
        }
    }

    private static void normalizeLegacyRoles(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "ALTER TABLE users MODIFY role ENUM('ADMIN','MEMBER','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'MEMBER'")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE users SET role = 'STUDENT' WHERE role = 'MEMBER'")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "ALTER TABLE users MODIFY role ENUM('ADMIN','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'STUDENT'")) {
            ps.executeUpdate();
        }
    }

    private static void ensureUserStateColumns(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS approved BOOLEAN NOT NULL DEFAULT FALSE")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT FALSE")) {
            ps.executeUpdate();
        }
    }

    private static void upsertDefaultUser(Connection connection,
                                          String username,
                                          String plainPassword,
                                          String fullName,
                                          String email,
                                          Role expectedRole) throws SQLException {
        String selectSql = "SELECT id, password, full_name, email, role, approved, active FROM users WHERE username = ?";
        try (PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setString(1, username);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String storedPassword = rs.getString("password");
                    String storedRole = rs.getString("role");
                    String storedFullName = rs.getString("full_name");
                    String storedEmail = rs.getString("email");
                    boolean approved = rs.getBoolean("approved");
                    boolean active = rs.getBoolean("active");
                    boolean needsPasswordUpdate = !PasswordUtil.verify(plainPassword, storedPassword);
                    boolean needsRoleUpdate = !Role.from(storedRole).equals(expectedRole);
                    boolean needsInfoUpdate = !fullName.equals(storedFullName) || !email.equals(storedEmail);
                    if (needsPasswordUpdate || needsRoleUpdate || needsInfoUpdate || !approved || !active) {
                        try (PreparedStatement update = connection.prepareStatement(
                                "UPDATE users SET password=?, full_name=?, email=?, role=?, approved=?, active=? WHERE id=?")) {
                            update.setString(1, PasswordUtil.hash(plainPassword));
                            update.setString(2, fullName);
                            update.setString(3, email);
                            update.setString(4, expectedRole.name());
                            update.setBoolean(5, true);
                            update.setBoolean(6, true);
                            update.setInt(7, id);
                            update.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO users(username, password, full_name, email, role, approved, active) VALUES (?, ?, ?, ?, ?, ?, ?)") ) {
                        insert.setString(1, username);
                        insert.setString(2, PasswordUtil.hash(plainPassword));
                        insert.setString(3, fullName);
                        insert.setString(4, email);
                        insert.setString(5, expectedRole.name());
                        insert.setBoolean(6, true);
                        insert.setBoolean(7, true);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }
}
