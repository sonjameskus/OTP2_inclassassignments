import java.sql.*;

public class MariaDBConnection {

    public static Connection connect() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/temperature_db",
                    "temperature",
                    "password"
            );
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
            return null;
        }
    }
}
