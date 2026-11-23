
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TempRecordDAOTest {

    @Test
    void savesTemperatureRecordUsingPreparedStatement() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString()))
                .thenReturn(statement);

        TempRecord record = new TempRecord(
                100.0,
                37.78,
                2,
                1,
                "en",
                new Timestamp(System.currentTimeMillis())
        );

        TempRecordDAO dao = new TempRecordDAO();

        try (MockedStatic<MariaDBConnection> db =
                     mockStatic(MariaDBConnection.class)) {

            db.when(MariaDBConnection::connect)
                    .thenReturn(connection);

            assertTrue(dao.save(record));

            db.verify(MariaDBConnection::connect);

            verify(connection).prepareStatement(
                    contains("INSERT INTO temp_records"));

            verify(statement).setDouble(1, 100.0);
            verify(statement).setDouble(2, 37.78);
            verify(statement).setInt(3, 2);
            verify(statement).setInt(4, 1);
            verify(statement).setTimestamp(eq(5), any(Timestamp.class));
            verify(statement).setString(6, "en");

            verify(statement).executeUpdate();
            verify(statement).close();
            verify(connection).close();
        }
    }

    @Test
    void handlesDatabaseErrorWithoutThrowing() throws Exception {
        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Test database error"));

        TempRecordDAO dao = new TempRecordDAO();

        TempRecord record = new TempRecord(
                100.0,
                37.78,
                2,
                1,
                "en",
                new Timestamp(System.currentTimeMillis())
        );

        try (MockedStatic<MariaDBConnection> db =
                     mockStatic(MariaDBConnection.class)) {

            db.when(MariaDBConnection::connect)
                    .thenReturn(connection);

            assertDoesNotThrow(() -> assertFalse(dao.save(record)));

            verify(connection).prepareStatement(anyString());
            verify(connection).close();
        }

    }

    @Test
    void getLastFiveReturnsRecordsFromDatabase() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Timestamp timestamp = Timestamp.valueOf("2026-11-23 12:00:00");

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getDouble("input_value")).thenReturn(100.0, 32.0);
        when(rs.getDouble("result_value")).thenReturn(212.0, 0.0);
        when(rs.getInt("from_unit_id")).thenReturn(1, 2);
        when(rs.getInt("to_unit_id")).thenReturn(2, 1);
        when(rs.getString("language")).thenReturn("en", "fi");
        when(rs.getTimestamp("created_at")).thenReturn(timestamp);

        try (var mocked = mockStatic(MariaDBConnection.class)) {
            mocked.when(MariaDBConnection::connect).thenReturn(connection);

            TempRecordDAO dao = new TempRecordDAO();

            List<TempRecord> records = dao.getLastFive();

            assertEquals(2, records.size());
            assertEquals(100.0, records.get(0).getInputValue());
            assertEquals(212.0, records.get(0).getResultValue());
            assertEquals(1, records.get(0).getFromUnitId());
            assertEquals(2, records.get(0).getToUnitId());
            assertEquals("en", records.get(0).getLanguage());
            assertEquals(timestamp, records.get(0).getCreatedAt());

            assertEquals(32.0, records.get(1).getInputValue());
            assertEquals(0.0, records.get(1).getResultValue());
            assertEquals("fi", records.get(1).getLanguage());

            verify(rs, times(3)).next();
        }
    }

    @Test
    void getLastFiveReturnsEmptyListWhenNoRecords() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (var mocked = mockStatic(MariaDBConnection.class)) {
            mocked.when(MariaDBConnection::connect).thenReturn(connection);

            TempRecordDAO dao = new TempRecordDAO();

            List<TempRecord> records = dao.getLastFive();

            assertNotNull(records);
            assertTrue(records.isEmpty());
        }
    }

    @Test
    void getLastFiveReturnsEmptyListWhenDatabaseFails() throws Exception {
        Connection connection = mock(Connection.class);

        when(connection.prepareStatement(anyString()))
                .thenThrow(new SQLException("Database error"));

        try (var mocked = mockStatic(MariaDBConnection.class)) {
            mocked.when(MariaDBConnection::connect).thenReturn(connection);

            TempRecordDAO dao = new TempRecordDAO();

            List<TempRecord> records = dao.getLastFive();

            assertNotNull(records);
            assertTrue(records.isEmpty());
        }
    }
}
