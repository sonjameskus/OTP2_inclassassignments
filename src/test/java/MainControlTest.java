
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

    // ORIGINAL 14 TESTS

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

    // 5 ADDITIONAL TESTS

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
}
