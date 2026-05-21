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
            ensureSchema(connection);
            normalizeLegacyRoles(connection);
            ensureUserStateColumns(connection);
            ensureDefaultBooks(connection);
            upsertDefaultUser(connection, "admin", "admin123", "Administrator", "admin@lib.local", Role.ADMIN);
            upsertDefaultUser(connection, "librarian", "admin123", "Library Staff", "librarian@lib.local", Role.LIBRARIAN);
            upsertDefaultUser(connection, "student", "member123", "Default Student", "student@lib.local", Role.STUDENT);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to ensure default users", e);
        }
    }

    private static void ensureSchema(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "full_name VARCHAR(100), " +
                        "email VARCHAR(100), " +
                        "role ENUM('ADMIN','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'STUDENT', " +
                        "approved BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "active BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS books (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "isbn VARCHAR(20) UNIQUE, " +
                        "title VARCHAR(200) NOT NULL, " +
                        "author VARCHAR(100) NOT NULL, " +
                        "category VARCHAR(50), " +
                        "total_copies INT NOT NULL DEFAULT 1, " +
                        "available_copies INT NOT NULL DEFAULT 1, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS borrow_records (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "book_id INT NOT NULL, " +
                        "borrow_date DATE NOT NULL, " +
                        "due_date DATE NOT NULL, " +
                        "return_date DATE, " +
                        "status ENUM('BORROWED','RETURNED','OVERDUE') NOT NULL DEFAULT 'BORROWED', " +
                        "FOREIGN KEY (user_id) REFERENCES users(id), " +
                        "FOREIGN KEY (book_id) REFERENCES books(id)" +
                        ") ENGINE=InnoDB")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "book_id INT NOT NULL, " +
                        "reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "notified BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "fulfilled BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id), " +
                        "FOREIGN KEY (book_id) REFERENCES books(id)" +
                        ") ENGINE=InnoDB")) {
            ps.executeUpdate();
        }
    }

    private static void normalizeLegacyRoles(Connection connection) throws SQLException {
        // Some MySQL versions do not support complex ALTER syntax; perform best-effort
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "ALTER TABLE users MODIFY role ENUM('ADMIN','MEMBER','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'MEMBER'")) {
                ps.executeUpdate();
            }
        } catch (SQLException ignored) {
            // ignore and continue
        }
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE users SET role = 'STUDENT' WHERE role = 'MEMBER'")) {
                ps.executeUpdate();
            }
        } catch (SQLException ignored) {
        }
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "ALTER TABLE users MODIFY role ENUM('ADMIN','LIBRARIAN','STUDENT') NOT NULL DEFAULT 'STUDENT'")) {
                ps.executeUpdate();
            }
        } catch (SQLException ignored) {
        }
    }

    private static void ensureUserStateColumns(Connection connection) throws SQLException {
        // Add columns only if they don't exist (compatibly across MySQL versions)
        java.sql.DatabaseMetaData md = connection.getMetaData();
        try (ResultSet cols = md.getColumns(connection.getCatalog(), null, "users", "approved")) {
            if (!cols.next()) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "ALTER TABLE users ADD approved BOOLEAN NOT NULL DEFAULT FALSE")) {
                    ps.executeUpdate();
                }
            }
        }
        try (ResultSet cols = md.getColumns(connection.getCatalog(), null, "users", "active")) {
            if (!cols.next()) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "ALTER TABLE users ADD active BOOLEAN NOT NULL DEFAULT FALSE")) {
                    ps.executeUpdate();
                }
            }
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

    private static void ensureDefaultBooks(Connection connection) throws SQLException {
        upsertDefaultBook(connection, "978-0132350884", "Clean Code", "Robert C. Martin", "Programming", 3, 3);
        upsertDefaultBook(connection, "978-0201633610", "Design Patterns", "Erich Gamma", "Programming", 2, 2);
        upsertDefaultBook(connection, "978-0061120084", "To Kill a Mockingbird", "Harper Lee", "Fiction", 4, 4);
        upsertDefaultBook(connection, "978-0596009205", "Head First Java", "Kathy Sierra", "Programming", 5, 5);
        upsertDefaultBook(connection, "978-0134494166", "Effective Java", "Joshua Bloch", "Programming", 3, 3);
        upsertDefaultBook(connection, "978-0262033848", "Introduction to Algorithms", "Cormen, Leiserson, Rivest, Stein", "Computer Science", 2, 2);
    }

    private static void upsertDefaultBook(Connection connection,
                                          String isbn,
                                          String title,
                                          String author,
                                          String category,
                                          int totalCopies,
                                          int availableCopies) throws SQLException {
        String selectSql = "SELECT id FROM books WHERE isbn = ?";
        try (PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setString(1, isbn);
            try (ResultSet rs = select.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO books(isbn,title,author,category,total_copies,available_copies) VALUES (?,?,?,?,?,?)")) {
                        insert.setString(1, isbn);
                        insert.setString(2, title);
                        insert.setString(3, author);
                        insert.setString(4, category);
                        insert.setInt(5, totalCopies);
                        insert.setInt(6, availableCopies);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }
}
