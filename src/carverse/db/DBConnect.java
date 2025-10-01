package carverse.db;

import java.sql.*;

public class DBConnect {
    static final String URL = "jdbc:mysql://localhost:3306/carrental"; // replace with your DB name
    static final String USER = "root";       // replace with your DB username
    static final String PASSWORD = "";   // replace with your DB password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
