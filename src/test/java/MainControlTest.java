import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private MainController controller;

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

        return method.invoke(controller, arguments);
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

        assertEquals(25.0, result);
    }

    @Test
    void testTemperatureBelowAbsoluteZero() {
        assertThrows(Exception.class, () ->
                callPrivateMethod(
                        "convert",
                        new Class[]{double.class, String.class, String.class},
                        -300.0, "Celsius", "Kelvin"
                )
        );
    }

    @Test
    void testUnknownFromUnit() {
        assertThrows(Exception.class, () ->
                callPrivateMethod(
                        "convert",
                        new Class[]{double.class, String.class, String.class},
                        20.0, "Unknown", "Celsius"
                )
        );
    }

    @Test
    void testUnknownToUnit() {
        assertThrows(Exception.class, () ->
                callPrivateMethod(
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
        assertThrows(Exception.class, () ->
                callPrivateMethod(
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
        assertThrows(Exception.class, () ->
                callPrivateMethod(
                        "getUnitId",
                        new Class[]{String.class},
                        "Unknown"
                )
        );
    }
}