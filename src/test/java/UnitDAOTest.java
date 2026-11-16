
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UnitDAOTest {

    @Test
    void translateReturnsTranslationsFromDatabase() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString()))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getInt("unit_id")).thenReturn(1, 2);
        when(resultSet.getString("lang")).thenReturn("fi", "fi");
        when(resultSet.getString("name"))
                .thenReturn("Celsius", "Fahrenheit");
        when(resultSet.getString("description"))
                .thenReturn("Celsius-asteet", "Fahrenheit-asteet");

        try (MockedStatic<MariaDBConnection> db =
                     mockStatic(MariaDBConnection.class)) {

            db.when(MariaDBConnection::connect).thenReturn(connection);

            List<UnitTranslation> translations = UnitDAO.translate("fi");

            assertEquals(2, translations.size());

            assertEquals(1, translations.get(0).getUnitId());
            assertEquals("fi", translations.get(0).getLang());
            assertEquals("Celsius", translations.get(0).getName());
            assertEquals("Celsius-asteet",
                    translations.get(0).getDesc());

            assertEquals(2, translations.get(1).getUnitId());
            assertEquals("Fahrenheit", translations.get(1).getName());

            verify(statement).setString(1, "fi");
            verify(statement).executeQuery();
        }
    }

    @Test
    void translateReturnsEmptyListWhenNoTranslationsExist()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString()))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        try (MockedStatic<MariaDBConnection> db =
                     mockStatic(MariaDBConnection.class)) {

            db.when(MariaDBConnection::connect).thenReturn(connection);

            List<UnitTranslation> translations = UnitDAO.translate("ja");

            assertNotNull(translations);
            assertTrue(translations.isEmpty());
        }
    }

    @Test
    void translateReturnsEmptyListWhenDatabaseErrorOccurs()
            throws Exception {

        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Test database error"));

        try (MockedStatic<MariaDBConnection> db =
                     mockStatic(MariaDBConnection.class)) {

            db.when(MariaDBConnection::connect).thenReturn(connection);

            List<UnitTranslation> translations = UnitDAO.translate("fi");

            assertNotNull(translations);
            assertTrue(translations.isEmpty());
        }
    }
}
