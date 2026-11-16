import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.sql.Timestamp;

public class MainController {

    @FXML private ComboBox<String> languageComboBox;
    @FXML private ComboBox<String> unitComboBox;
    @FXML private ComboBox<String> toUnitComboBox;
    @FXML private TextField temperatureField;
    @FXML private Label conversionResult;
    @FXML private Label name;
    @FXML private Label db_text;
    @FXML private Button submit;
    @FXML private Label unitDescription;
    @FXML private ListView<String> historyListView;

    private TemperatureConverter converter = new TemperatureConverter();
    private TempRecordDAO dao = new TempRecordDAO();
    private UnitDAO uDao = new UnitDAO();

    void setDao(TempRecordDAO dao) {
        this.dao = dao;
    }

    private Map<String, String> displayToUnit = new HashMap<>();
    private Map<String, String> unitToDisplay = new HashMap<>();
    private Map<String, String> unitDesc = new HashMap<>();
    private ResourceBundle bundle;
    private String currentLanguage = "en";

    @FXML
    public void initialize() {

        languageComboBox.getItems().addAll(
                "English",
                "suomeksi",
                "svenska",
                "日本語",
                "عربي"
        );

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

        unitComboBox.setOnAction(event -> {
            String selected = unitComboBox.getValue();

            if (selected != null) {
                String unitKey = displayToUnit.get(selected);
                unitDescription.setText(unitDesc.get(unitKey));
            }
        });
    }

    private void loadLanguage(String language, String country) {
        currentLanguage = language;

        String from = "Celsius";
        String to = "Fahrenheit";

        if (!displayToUnit.isEmpty()) {
            if (unitComboBox.getValue() != null) {
                from = getUnitValue(unitComboBox.getValue());
            }

            if (toUnitComboBox.getValue() != null) {
                to = getUnitValue(toUnitComboBox.getValue());
            }
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

        updateUnitNames(language, from, to);
    }

    private void updateUnitNames(String lang, String from, String to) {

        List<UnitTranslation> translations =
                UnitDAO.translate(lang);

        displayToUnit.clear();
        unitToDisplay.clear();
        unitDesc.clear();

        for (UnitTranslation t : translations) {

            String unitKey = getUnitKeyById(t.getUnitId());

            displayToUnit.put(t.getName(), unitKey);
            unitToDisplay.put(unitKey, t.getName());
            unitDesc.put(unitKey, t.getDesc());
        }

        unitComboBox.getItems().setAll(
                unitToDisplay.get("Celsius"),
                unitToDisplay.get("Fahrenheit"),
                unitToDisplay.get("Kelvin")
        );

        toUnitComboBox.getItems().setAll(
                unitToDisplay.get("Celsius"),
                unitToDisplay.get("Fahrenheit"),
                unitToDisplay.get("Kelvin")
        );

        unitComboBox.setValue(unitToDisplay.get(from));
        toUnitComboBox.setValue(unitToDisplay.get(to));

        unitComboBox.setPromptText(bundle.getString("from"));
        toUnitComboBox.setPromptText(bundle.getString("to"));

        unitDescription.setText(
                unitDesc.get(unitComboBox.getValue() == null
                        ? "Celsius"
                        : displayToUnit.get(unitComboBox.getValue()))
        );
    }

    private String getUnitValue(String translatedUnit) {

        String unit = displayToUnit.get(translatedUnit);

        if (unit == null) {
            throw new IllegalArgumentException(
                    "Unknown temperature unit"
            );
        }

        return unit;
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

    private String getUnitKeyById(int id) {

        switch (id) {
            case 1:
                return "Celsius";

            case 2:
                return "Fahrenheit";

            case 3:
                return "Kelvin";

            default:
                throw new IllegalArgumentException(
                        "Unknown unit ID: " + id
                );
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

            String resultUnit = unitToDisplay.get(to);

            conversionResult.setText(
                    bundle.getString("result") + " " +
                            String.format(Locale.US, "%.2f", result) +
                            " " + resultUnit
            );

            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

            TempRecord record = new TempRecord(
                    input,
                    result,
                    getUnitId(from),
                    getUnitId(to),
                    currentLanguage,
                    createdAt
            );

            if (dao.save(record)) {
                db_text.setText(bundle.getString("db_saved"));
                updateHistory();
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

    private void updateHistory() {
        historyListView.getItems().clear();

        List<TempRecord> records = dao.getLastFive();

        for (TempRecord record : records) {
            String from = getUnitKeyById(record.getFromUnitId());
            String to = getUnitKeyById(record.getToUnitId());

            String fromName = unitToDisplay.get(from);
            String toName = unitToDisplay.get(to);

            String historyText =
                    record.getInputValue() + " " + fromName +
                            " → " +
                            record.getResultValue() + " " + toName;

            historyListView.getItems().add(historyText);
        }
    }
}
