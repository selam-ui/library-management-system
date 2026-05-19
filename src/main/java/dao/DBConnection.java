package dao;

import exception.DatabaseException;
import util.DBConfig;
import util.DatabaseInitializer;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection connection;

    public static Connection get() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DBConfig.get("db.driver"));
                connection = DriverManager.getConnection(
                        DBConfig.get("db.url"),
                        DBConfig.get("db.user"),
                        DBConfig.get("db.password"));
                DatabaseInitializer.ensureDefaultUsers(connection);
            }
            return connection;
        } catch (Exception e) {
            throw new DatabaseException("Unable to connect to database", e);
        }
    }
}
