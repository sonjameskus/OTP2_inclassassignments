import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TempRecordDAO {

    private static final Logger msg = Logger.getLogger(TempRecordDAO.class.getName());

    public boolean save(TempRecord record) {

        String sql = """
                INSERT INTO temp_records
                (input_value, result_value, from_unit_id, to_unit_id, created_at, language)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = MariaDBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, record.getInputValue());
            statement.setDouble(2, record.getResultValue());
            statement.setInt(3, record.getFromUnitId());
            statement.setInt(4, record.getToUnitId());
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.setString(6, record.getLanguage());

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            msg.info("Error");

            return false;
        }
    }


    public List<TempRecord> getLastFive() {
        List<TempRecord> records = new ArrayList<>();

        String sql = "SELECT * FROM temp_records " +
                "ORDER BY created_at DESC LIMIT 5";

        try (Connection connection = MariaDBConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                TempRecord record = new TempRecord(
                        rs.getDouble("input_value"),
                        rs.getDouble("result_value"),
                        rs.getInt("from_unit_id"),
                        rs.getInt("to_unit_id"),
                        rs.getString("language"),
                        rs.getTimestamp("created_at")
                );

                records.add(record);
            }

        } catch (SQLException e) {
            msg.info("Error");
        }

        return records;
    }
}