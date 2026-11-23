
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UnitDAO {

    private static final Logger msg = Logger.getLogger(UnitDAO.class.getName());

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
            msg.info("Error");
        }

        return translations;
    }
}
