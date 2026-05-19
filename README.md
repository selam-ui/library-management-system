# Library Management System (JavaFX + Maven + MySQL)

## Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8.x running locally

## Setup
1. Create the database:
   ```
   mysql -u root -p < database/schema.sql
   ```
2. Edit `resources/db.properties` with your MySQL username/password.
3. Run:
   ```
   mvn clean javafx:run
   ```

## Default login
- Admin: `admin` / `admin123`
- Librarian: `librarian` / `admin123`
- Student: `student` / `member123`

## New feature
- Borrow and return books from the dashboard using the new Borrow Books screen.
