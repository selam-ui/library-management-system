CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    role ENUM('ADMIN','MEMBER') NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS borrow_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('BORROWED','RETURNED','OVERDUE') NOT NULL DEFAULT 'BORROWED',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Default users (passwords are BCrypt hashes for 'admin123' and 'member123')
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin',  '$2a$10$7EqJtq98hPqEX7fNZaFWoO9b3vL0vV2pY0pH3wYf7s0Q0qZmF5fS6', 'Administrator', 'admin@lib.local',  'ADMIN'),
('member', '$2a$10$DowQk0Bd4qK5kqMq8GxJ6e8WJj1Yk8eQ2T6r2Y3KQ7m5n0qHc4yWa', 'Default Member', 'member@lib.local', 'MEMBER')
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO books (isbn, title, author, category, total_copies, available_copies) VALUES
('978-0132350884','Clean Code','Robert C. Martin','Programming',3,3),
('978-0201633610','Design Patterns','Erich Gamma','Programming',2,2),
('978-0061120084','To Kill a Mockingbird','Harper Lee','Fiction',4,4)
ON DUPLICATE KEY UPDATE isbn=isbn;
