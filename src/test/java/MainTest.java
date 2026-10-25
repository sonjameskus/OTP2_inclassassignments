import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private InputStream originalIn;
    private PrintStream originalOut;
    private Locale originalLocale;

    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        originalLocale = Locale.getDefault();

        output = new ByteArrayOutputStream();

        System.setOut(new PrintStream(
                output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    private String runMain(String input) {
        System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));

        Main.main(new String[0]);

        return output.toString(StandardCharsets.UTF_8);
    }

    private String message(Locale locale, String key) {
        return ResourceBundle
                .getBundle("MessagesBundle", locale)
                .getString(key);
    }


    @Test
    void selectsEnglish() {
        String result = runMain("1\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.US, "lang_chosen")));
    }

    @Test
    void selectsFinnish() {
        String result = runMain("2\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.forLanguageTag("fi-FI"),
                        "lang_chosen")));
    }

    @Test
    void selectsSwedish() {
        String result = runMain("3\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.forLanguageTag("sv-SE"),
                        "lang_chosen")));
    }

    @Test
    void selectsJapanese() {
        String result = runMain("4\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.JAPAN, "lang_chosen")));
    }

    @Test
    void invalidLanguageChoicePrintsError() {
        Locale.setDefault(Locale.US);

        String result = runMain("9\n1\n0\n");

        assertTrue(result.contains("Invalid choice."));
    }

    @Test
    void invalidLanguageUsesDefaultLocale() {
        Locale.setDefault(Locale.US);

        String result = runMain("9\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.US, "lang_chosen")));
    }


    @Test
    void convertsCelsiusToFahrenheit() {
        String result = runMain("1\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.US, "celsius")));
        assertTrue(result.contains(
                message(Locale.US, "ctof")));
        assertTrue(result.contains("32.0"));
    }

    @Test
    void convertsPositiveCelsiusTemperature() {
        String result = runMain("1\n1\n100\n");

        assertTrue(result.contains("212.0"));
    }

    @Test
    void convertsNegativeCelsiusTemperature() {
        String result = runMain("1\n1\n-40\n");

        assertTrue(result.contains("-40.0"));
    }

    @Test
    void convertsDecimalCelsiusTemperature() {
        String result = runMain("1\n1\n37.5\n");

        assertTrue(result.contains("99.5"));
    }

    @Test
    void printsOriginalCelsiusInput() {
        String result = runMain("1\n1\n25\n");

        assertTrue(result.contains("25.0"));
    }


    @Test
    void convertsFahrenheitToCelsius() {
        String result = runMain("1\n2\n32\n");

        assertTrue(result.contains(
                message(Locale.US, "fahrenheit")));
        assertTrue(result.contains(
                message(Locale.US, "ftoc")));
        assertTrue(result.contains("0.0"));
    }

    @Test
    void convertsBoilingPointToCelsius() {
        String result = runMain("1\n2\n212\n");

        assertTrue(result.contains("100.0"));
    }

    @Test
    void convertsNegativeFahrenheitTemperature() {
        String result = runMain("1\n2\n-40\n");

        assertTrue(result.contains("-40.0"));
    }

    @Test
    void convertsDecimalFahrenheitTemperature() {
        String result = runMain("1\n2\n98.6\n");

        assertTrue(result.contains("37.0"));
    }

    @Test
    void printsOriginalFahrenheitInput() {
        String result = runMain("1\n2\n77\n");

        assertTrue(result.contains("77.0"));
    }


    @Test
    void invalidConversionChoicePrintsError() {
        String result = runMain("1\n3\n");

        assertTrue(result.contains(
                message(Locale.US, "error")));
    }

    @Test
    void zeroConversionChoicePrintsError() {
        String result = runMain("1\n0\n");

        assertTrue(result.contains(
                message(Locale.US, "error")));
    }

    @Test
    void negativeConversionChoicePrintsError() {
        String result = runMain("1\n-1\n");

        assertTrue(result.contains(
                message(Locale.US, "error")));
    }


    @Test
    void printsLanguageSelectionPrompt() {
        String result = runMain("1\n1\n0\n");

        assertTrue(result.contains(
                "Select language 1. English 2. suomeksi "
                        + "3. svenska 4. 日本語"));
    }

    @Test
    void printsConversionQuestion() {
        String result = runMain("1\n1\n0\n");

        assertTrue(result.contains(
                message(Locale.US, "question")));
    }

    @Test
    void displaysFinnishConversionMessages() {
        String result = runMain("2\n2\n32\n");

        assertTrue(result.contains(
                message(Locale.forLanguageTag("fi-FI"),
                        "fahrenheit")));
        assertTrue(result.contains(
                message(Locale.forLanguageTag("fi-FI"),
                        "ftoc")));
    }

    @Test
    void displaysSwedishConversionMessages() {
        String result = runMain("3\n2\n32\n");

        assertTrue(result.contains(
                message(Locale.forLanguageTag("sv-SE"),
                        "fahrenheit")));
        assertTrue(result.contains(
                message(Locale.forLanguageTag("sv-SE"),
                        "ftoc")));
    }

    @Test
    void displaysJapaneseConversionMessages() {
        String result = runMain("4\n2\n32\n");

        assertTrue(result.contains(
                message(Locale.JAPAN, "fahrenheit")));
        assertTrue(result.contains(
                message(Locale.JAPAN, "ftoc")));
    }
}
