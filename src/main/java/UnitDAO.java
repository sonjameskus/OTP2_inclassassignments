
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    public static List<UnitTranslation> translate(String lang) {

        List<UnitTranslation> translations = new ArrayList<>();

        String sql = "SELECT unit_id, lang, name, description "
                + "FROM temperature_unit_translations "
                + "WHERE lang = ?";

        try (Connection connection = MariaDBConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, lang);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                UnitTranslation t = new UnitTranslation(
                        rs.getInt("unit_id"),
                        rs.getString("lang"),
                        rs.getString("name"),
                        rs.getString("description")
                );

                translations.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return translations;
    }
}
