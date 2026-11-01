import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    @FXML private ComboBox<String> languageComboBox;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private ComboBox<String> toUnitComboBox;
    @FXML private TextField temperatureField;
    @FXML private Label conversionResult;
    @FXML private Label name;
    @FXML private Label db_text;
    @FXML private Button submit;

    private TemperatureConverter converter = new TemperatureConverter();
    private TempRecordDAO dao = new TempRecordDAO();

    private ResourceBundle bundle;

    @FXML
    public void initialize() {

        languageComboBox.getItems().addAll(
                "English",
                "suomeksi",
                "svenska",
                "日本語",
                "عربي"
        );

        unitComboBox.getItems().addAll(
                "Celsius", "Fahrenheit", "Kelvin"
        );

        toUnitComboBox.getItems().addAll(
                "Celsius", "Fahrenheit", "Kelvin"
        );

        unitComboBox.setValue("Celsius");
        toUnitComboBox.setValue("Fahrenheit");

        languageComboBox.setValue("English");

        loadLanguage("en", "US");

        languageComboBox.setOnAction(event -> {

            String language = languageComboBox.getValue();

            switch (language) {
                case "English":
                    loadLanguage("en", "US");
                    break;

                case "suomeksi":
                    loadLanguage("fi", "FI");
                    break;

                case "svenska":
                    loadLanguage("sv", "SE");
                    break;

                case "日本語":
                    loadLanguage("ja", "JP");
                    break;

                case "عربي":
                    loadLanguage("ar", "EG");
                    break;
            }
        });
    }

    private void loadLanguage(String language, String country) {

        String from = "Celsius";
        String to = "Fahrenheit";

        if (bundle != null) {
            from = getUnitValue(unitComboBox.getValue());
            to = getUnitValue(toUnitComboBox.getValue());
        }

        Locale locale = new Locale(language, country);

        bundle = ResourceBundle.getBundle(
                "MessagesBundle", locale
        );

        name.setText(bundle.getString("title"));
        submit.setText(bundle.getString("convert"));

        temperatureField.setPromptText(
                bundle.getString("temperature")
        );

        conversionResult.setText(
                bundle.getString("result")
        );

        db_text.setText("");

        updateUnitNames(from, to);
    }

    private void updateUnitNames(String from, String to) {

        unitComboBox.getItems().setAll(
                bundle.getString("celsius"),
                bundle.getString("fahrenheit"),
                bundle.getString("kelvin")
        );

        toUnitComboBox.getItems().setAll(
                bundle.getString("celsius"),
                bundle.getString("fahrenheit"),
                bundle.getString("kelvin")
        );

        unitComboBox.setValue(
                bundle.getString(getUnitKey(from))
        );

        toUnitComboBox.setValue(
                bundle.getString(getUnitKey(to))
        );

        unitComboBox.setPromptText(bundle.getString("from"));
        toUnitComboBox.setPromptText(bundle.getString("to"));
    }

    private String getUnitValue(String translatedUnit) {

        if (translatedUnit.equals(bundle.getString("celsius"))) {
            return "Celsius";
        }

        if (translatedUnit.equals(bundle.getString("fahrenheit"))) {
            return "Fahrenheit";
        }

        if (translatedUnit.equals(bundle.getString("kelvin"))) {
            return "Kelvin";
        }

        throw new IllegalArgumentException("Unknown temperature unit");
    }

    private String getUnitKey(String unit) {

        switch (unit) {
            case "Celsius":
                return "celsius";

            case "Fahrenheit":
                return "fahrenheit";

            case "Kelvin":
                return "kelvin";

            default:
                throw new IllegalArgumentException("Unknown unit");
        }
    }

    private int getUnitId(String unit) {

        switch (unit) {
            case "Celsius":
                return 1;

            case "Fahrenheit":
                return 2;

            case "Kelvin":
                return 3;

            default:
                throw new IllegalArgumentException("Unknown unit");
        }
    }

    @FXML
    private void handleConvert() {

        try {

            double input = Double.parseDouble(
                    temperatureField.getText().trim()
            );

            if (unitComboBox.getValue() == null ||
                    toUnitComboBox.getValue() == null) {

                conversionResult.setText(bundle.getString("error"));
                return;
            }

            String from = getUnitValue(unitComboBox.getValue());
            String to = getUnitValue(toUnitComboBox.getValue());

            if (from.equals("Kelvin") && input < 0) {
                conversionResult.setText(bundle.getString("error"));
                return;
            }

            double result = convert(input, from, to);

            String resultUnit = bundle.getString(getUnitKey(to));

            conversionResult.setText(
                    bundle.getString("result") + " " +
                            String.format(Locale.US, "%.2f", result) +
                            " " + resultUnit
            );

            TempRecord record = new TempRecord(
                    input,
                    result,
                    getUnitId(from),
                    getUnitId(to)
            );

            if (dao.save(record)) {
                db_text.setText(bundle.getString("db_saved"));
            } else {
                db_text.setText(bundle.getString("db_error"));
            }

        } catch (NumberFormatException e) {

            conversionResult.setText(bundle.getString("error"));

        } catch (IllegalArgumentException e) {

            conversionResult.setText(bundle.getString("error"));
        }
    }

    private double convert(double value, String from, String to) {

        if (from.equals(to)) {
            return value;
        }

        double celsius;

        switch (from) {

            case "Celsius":
                celsius = value;
                break;

            case "Fahrenheit":
                celsius = converter.fahrenheitToCelsius(value);
                break;

            case "Kelvin":
                celsius = converter.kelvinToCelsius(value);
                break;

            default:
                throw new IllegalArgumentException("Unknown unit");
        }

        if (celsius < -273.15) {
            throw new IllegalArgumentException("Temperature below absolute zero");
        }

        switch (to) {

            case "Celsius":
                return celsius;

            case "Fahrenheit":
                return converter.celsiusToFahrenheit(celsius);

            case "Kelvin":
                return celsius + 273.15;

            default:
                throw new IllegalArgumentException("Unknown unit");
        }
    }
}
