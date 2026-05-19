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
- Member: `member` / `member123`
