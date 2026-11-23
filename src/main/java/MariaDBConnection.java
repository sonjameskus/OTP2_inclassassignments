import java.sql.*;
import java.util.logging.Logger;

public class MariaDBConnection {

    private static final Logger msg = Logger.getLogger(MariaDBConnection.class.getName());

    public static Connection connect() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/temperature_db",
                    "temperature",
                    "password"
            );
        } catch (SQLException e) {
            msg.info("Connection failed.");
            return null;
        }
    }
}
