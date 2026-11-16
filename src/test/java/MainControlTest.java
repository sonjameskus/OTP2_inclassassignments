import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainControllerTest {

    private MainController controller;

    @BeforeAll
    static void startJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() {
        controller = new MainController();
    }

    private Object callPrivateMethod(
            String methodName,
            Class<?>[] parameterTypes,
            Object... arguments
    ) throws Exception {

        Method method = MainController.class.getDeclaredMethod(
                methodName, parameterTypes
        );

        method.setAccessible(true);

        try {
            return method.invoke(controller, arguments);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = MainController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private Object getField(String fieldName) throws Exception {
        Field field = MainController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(controller);
    }


    @Test
    void testCelsiusToFahrenheit() throws Exception {
        double result = (double) callPrivateMethod(
                "convert",
                new Class[]{double.class, String.class, String.class},
                0.0, "Celsius", "Fahrenheit"
        );
        assertEquals(32.0, result, 0.01);
    }

    @Test
    void testFahrenheitToCelsius() throws Exception {
        double result = (double) callPrivateMethod(
                "convert",
                new Class[]{double.class, String.class, String.class},
                32.0, "Fahrenheit", "Celsius"
        );
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void testKelvinToCelsius() throws Exception {
        double result = (double) callPrivateMethod(
                "convert",
                new Class[]{double.class, String.class, String.class},
                273.15, "Kelvin", "Celsius"
        );
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void testCelsiusToKelvin() throws Exception {
        double result = (double) callPrivateMethod(
                "convert",
                new Class[]{double.class, String.class, String.class},
                0.0, "Celsius", "Kelvin"
        );
        assertEquals(273.15, result, 0.01);
    }

    @Test
    void testSameUnitConversion() throws Exception {
        double result = (double) callPrivateMethod(
                "convert",
                new Class[]{double.class, String.class, String.class},
                25.0, "Celsius", "Celsius"
        );
        assertEquals(25.0, result, 0.01);
    }

    @Test
    void testTemperatureBelowAbsoluteZero() {
        Exception exception = assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "convert",
                        new Class[]{double.class, String.class, String.class},
                        -300.0, "Celsius", "Kelvin"
                )
        );
        assertTrue(exception.getMessage().contains("absolute zero"));
    }

    @Test
    void testUnknownFromUnit() {
        assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "convert",
                        new Class[]{double.class, String.class, String.class},
                        20.0, "Unknown", "Celsius"
                )
        );
    }

    @Test
    void testUnknownToUnit() {
        assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "convert",
                        new Class[]{double.class, String.class, String.class},
                        20.0, "Celsius", "Unknown"
                )
        );
    }

    @Test
    void testGetUnitKey() throws Exception {
        assertEquals("celsius",
                callPrivateMethod("getUnitKey",
                        new Class[]{String.class}, "Celsius"));
        assertEquals("fahrenheit",
                callPrivateMethod("getUnitKey",
                        new Class[]{String.class}, "Fahrenheit"));
        assertEquals("kelvin",
                callPrivateMethod("getUnitKey",
                        new Class[]{String.class}, "Kelvin"));
    }

    @Test
    void testGetUnitKeyInvalid() {
        assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "getUnitKey",
                        new Class[]{String.class},
                        "Unknown"
                )
        );
    }

    @Test
    void testGetUnitId() throws Exception {
        assertEquals(1,
                callPrivateMethod("getUnitId",
                        new Class[]{String.class}, "Celsius"));
        assertEquals(2,
                callPrivateMethod("getUnitId",
                        new Class[]{String.class}, "Fahrenheit"));
        assertEquals(3,
                callPrivateMethod("getUnitId",
                        new Class[]{String.class}, "Kelvin"));
    }

    @Test
    void testGetUnitIdInvalid() {
        assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "getUnitId",
                        new Class[]{String.class},
                        "Unknown"
                )
        );
    }

    @Test
    void testGetUnitKeyById() throws Exception {
        assertEquals("Celsius",
                callPrivateMethod("getUnitKeyById",
                        new Class[]{int.class}, 1));
        assertEquals("Fahrenheit",
                callPrivateMethod("getUnitKeyById",
                        new Class[]{int.class}, 2));
        assertEquals("Kelvin",
                callPrivateMethod("getUnitKeyById",
                        new Class[]{int.class}, 3));
    }

    @Test
    void testGetUnitKeyByIdInvalid() {
        assertThrows(
                Exception.class,
                () -> callPrivateMethod(
                        "getUnitKeyById",
                        new Class[]{int.class},
                        99
                )
        );
    }


    @Test
    void testGetUnitValueCelsius() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Celsius", "Celsius");
        setField("displayToUnit", map);

        assertEquals("Celsius",
                callPrivateMethod("getUnitValue",
                        new Class[]{String.class}, "Celsius"));
    }

    @Test
    void testGetUnitValueFahrenheit() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Fahrenheit", "Fahrenheit");
        setField("displayToUnit", map);

        assertEquals("Fahrenheit",
                callPrivateMethod("getUnitValue",
                        new Class[]{String.class}, "Fahrenheit"));
    }

    @Test
    void testGetUnitValueKelvin() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Kelvin", "Kelvin");
        setField("displayToUnit", map);

        assertEquals("Kelvin",
                callPrivateMethod("getUnitValue",
                        new Class[]{String.class}, "Kelvin"));
    }

    @Test
    void testGetUnitValueUnknown() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("Celsius", "Celsius");
                    setField("displayToUnit", map);
                    callPrivateMethod("getUnitValue",
                            new Class[]{String.class}, "Unknown");
                }


        );
    }

    @Test
    void testUpdateUnitNames() throws Exception {
        runOnFxThread(() -> {
            prepareController(mock(TempRecordDAO.class));

            try (MockedStatic<UnitDAO> mocked = mockStatic(UnitDAO.class)) {
                mocked.when(() -> UnitDAO.translate("en"))
                        .thenReturn(translations());

                callPrivateMethod("updateUnitNames",
                        new Class[]{String.class, String.class, String.class},
                        "en", "Celsius", "Fahrenheit");

                ComboBox<String> from =
                        (ComboBox<String>) getField("unitComboBox");

                ComboBox<String> to =
                        (ComboBox<String>) getField("toUnitComboBox");

                assertEquals(3, from.getItems().size());
                assertEquals(3, to.getItems().size());
                assertEquals("Celsius", from.getValue());
                assertEquals("Fahrenheit", to.getValue());
            }
            return null;
        });
    }

    @Test
    void testLoadLanguage() throws Exception {
        runOnFxThread(() -> {
            prepareController(mock(TempRecordDAO.class));

            try (MockedStatic<UnitDAO> mocked = mockStatic(UnitDAO.class)) {
                mocked.when(() -> UnitDAO.translate("en"))
                        .thenReturn(translations());

                callPrivateMethod("loadLanguage",
                        new Class[]{String.class, String.class},
                        "en", "US");

                assertEquals("en", getField("currentLanguage"));

                ComboBox<String> from =
                        (ComboBox<String>) getField("unitComboBox");

                assertNotNull(from.getValue());
                assertEquals(3, from.getItems().size());
            }
            return null;
        });
    }

    @Test
    void testInitialize() throws Exception {
        runOnFxThread(() -> {
            prepareController(mock(TempRecordDAO.class));

            try (MockedStatic<UnitDAO> mocked = mockStatic(UnitDAO.class)) {
                mocked.when(() -> UnitDAO.translate("en"))
                        .thenReturn(translations());

                callPrivateMethod("initialize", new Class[]{});

                ComboBox<String> languages =
                        (ComboBox<String>) getField("languageComboBox");

                assertEquals(5, languages.getItems().size());
                assertEquals("English", languages.getValue());

                ComboBox<String> units =
                        (ComboBox<String>) getField("unitComboBox");

                assertEquals(3, units.getItems().size());
            }
            return null;
        });
    }

    @Test
    void testHandleConvertSuccessfulConversion() throws Exception {
        runOnFxThread(() -> {
            TempRecordDAO dao = mock(TempRecordDAO.class);
            when(dao.save(any(TempRecord.class))).thenReturn(true);
            when(dao.getLastFive()).thenReturn(List.of());

            prepareController(dao);

            Map<String, String> display = new HashMap<>();
            display.put("Celsius", "Celsius");
            display.put("Fahrenheit", "Fahrenheit");
            display.put("Kelvin", "Kelvin");

            setField("displayToUnit", display);
            setField("unitToDisplay", display);

            ((TextField) getField("temperatureField")).setText("100");

            ((ComboBox<String>) getField("unitComboBox"))
                    .setValue("Celsius");

            ((ComboBox<String>) getField("toUnitComboBox"))
                    .setValue("Fahrenheit");

            callPrivateMethod("handleConvert", new Class[]{});

            Label result = (Label) getField("conversionResult");

            assertTrue(result.getText().contains("212.00"));
            verify(dao).save(any(TempRecord.class));
            verify(dao).getLastFive();

            return null;
        });
    }

    @Test
    void testHandleConvertInvalidInput() throws Exception {
        runOnFxThread(() -> {
            TempRecordDAO dao = mock(TempRecordDAO.class);
            prepareController(dao);

            ((TextField) getField("temperatureField")).setText("abc");

            callPrivateMethod("handleConvert", new Class[]{});

            Label result = (Label) getField("conversionResult");

            assertFalse(result.getText().isEmpty());
            verify(dao, never()).save(any(TempRecord.class));

            return null;
        });
    }

    @Test
    void testHandleConvertNegativeKelvin() throws Exception {
        runOnFxThread(() -> {
            TempRecordDAO dao = mock(TempRecordDAO.class);
            prepareController(dao);

            Map<String, String> display = new HashMap<>();
            display.put("Celsius", "Celsius");
            display.put("Fahrenheit", "Fahrenheit");
            display.put("Kelvin", "Kelvin");

            setField("displayToUnit", display);
            setField("unitToDisplay", display);

            ((TextField) getField("temperatureField")).setText("-10");

            ((ComboBox<String>) getField("unitComboBox"))
                    .setValue("Kelvin");

            ((ComboBox<String>) getField("toUnitComboBox"))
                    .setValue("Celsius");

            callPrivateMethod("handleConvert", new Class[]{});

            Label result = (Label) getField("conversionResult");

            assertFalse(result.getText().isEmpty());
            verify(dao, never()).save(any(TempRecord.class));

            return null;
        });
    }

    @Test
    void testHandleConvertDatabaseSaveFails() throws Exception {
        runOnFxThread(() -> {
            TempRecordDAO dao = mock(TempRecordDAO.class);
            when(dao.save(any(TempRecord.class))).thenReturn(false);

            prepareController(dao);

            Map<String, String> display = new HashMap<>();
            display.put("Celsius", "Celsius");
            display.put("Fahrenheit", "Fahrenheit");
            display.put("Kelvin", "Kelvin");

            setField("displayToUnit", display);
            setField("unitToDisplay", display);

            ((TextField) getField("temperatureField")).setText("100");

            ((ComboBox<String>) getField("unitComboBox"))
                    .setValue("Celsius");

            ((ComboBox<String>) getField("toUnitComboBox"))
                    .setValue("Fahrenheit");

            callPrivateMethod("handleConvert", new Class[]{});

            Label message = (Label) getField("db_text");

            assertFalse(message.getText().isEmpty());
            verify(dao).save(any(TempRecord.class));
            verify(dao, never()).getLastFive();

            return null;
        });
    }

    @Test
    void testUpdateHistory() throws Exception {
        runOnFxThread(() -> {
            TempRecordDAO dao = mock(TempRecordDAO.class);

            TempRecord record = new TempRecord(
                    100.0,
                    37.78,
                    2,
                    1,
                    "en",
                    new Timestamp(System.currentTimeMillis())
            );

            when(dao.getLastFive()).thenReturn(List.of(record));

            prepareController(dao);

            Map<String, String> display = new HashMap<>();
            display.put("Celsius", "Celsius");
            display.put("Fahrenheit", "Fahrenheit");
            display.put("Kelvin", "Kelvin");

            setField("unitToDisplay", display);

            callPrivateMethod("updateHistory", new Class[]{});

            ListView<String> history =
                    (ListView<String>) getField("historyListView");

            assertEquals(1, history.getItems().size());
            assertTrue(history.getItems().get(0).contains("Fahrenheit"));
            assertTrue(history.getItems().get(0).contains("Celsius"));

            return null;
        });
    }

    @Test
    void testGetUnitValueNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    setField("displayToUnit", new HashMap<String, String>());
                    callPrivateMethod("getUnitValue",
                            new Class[]{String.class}, (Object) null);
                }
        );
    }

    private <T> T runOnFxThread(Callable<T> task) throws Exception {
        FutureTask<T> future = new FutureTask<>(task);
        Platform.runLater(future);
        return future.get(10, TimeUnit.SECONDS);
    }

    private void prepareController(TempRecordDAO dao) throws Exception {
        setField("languageComboBox", new ComboBox<String>());
        setField("unitComboBox", new ComboBox<String>());
        setField("toUnitComboBox", new ComboBox<String>());
        setField("temperatureField", new TextField());
        setField("conversionResult", new Label());
        setField("name", new Label());
        setField("db_text", new Label());
        setField("submit", new Button());
        setField("unitDescription", new Label());
        setField("historyListView", new ListView<String>());

        setField("bundle",
                ResourceBundle.getBundle("MessagesBundle", Locale.US));
        setField("currentLanguage", "en");

        controller.setDao(dao);
    }

    private List<UnitTranslation> translations() {
        return List.of(
                new UnitTranslation(1, "en", "Celsius", "Celsius temperature"),
                new UnitTranslation(2, "en", "Fahrenheit", "Fahrenheit temperature"),
                new UnitTranslation(3, "en", "Kelvin", "Kelvin temperature")
        );
    }
}
