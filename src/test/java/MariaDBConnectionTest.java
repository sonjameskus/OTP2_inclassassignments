import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MariaDBConnectionTest {

    @Test
    void testConnectSuccess() throws SQLException {
        Connection mockConnection = mock(Connection.class);

        try (MockedStatic<DriverManager> driverManager =
                     mockStatic(DriverManager.class)) {

            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/temperature_db",
                    "temperature",
                    "password"
            )).thenReturn(mockConnection);

            Connection result = MariaDBConnection.connect();

            assertNotNull(result);
            assertSame(mockConnection, result);
        }
    }

    @Test
    void testConnectFailure() throws SQLException {

        SQLException exception = new SQLException("Connection failed");

        try (MockedStatic<DriverManager> driverManager =
                     mockStatic(DriverManager.class)) {

            driverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/temperature_db",
                    "temperature",
                    "password"
            )).thenThrow(exception);

            Connection result = MariaDBConnection.connect();

            assertNull(result);
        }
    }
}