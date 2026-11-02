package busxtrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/busxtrack";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "BusX1234";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J jar is not added to the project.", e);
        }

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
