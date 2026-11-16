
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitTranslationTest {

    @Test
    void constructorStoresAllValues() {
        UnitTranslation translation = new UnitTranslation(
                1,
                "fi",
                "Celsius",
                "Lämpötila Celsius-asteina"
        );

        assertEquals(1, translation.getUnitId());
        assertEquals("fi", translation.getLang());
        assertEquals("Celsius", translation.getName());
        assertEquals("Lämpötila Celsius-asteina", translation.getDesc());
    }

    @Test
    void settersUpdateAllValues() {
        UnitTranslation translation = new UnitTranslation(
                1,
                "en",
                "Celsius",
                "Temperature in Celsius"
        );

        translation.setUnitId(2);
        translation.setLang("fi");
        translation.setName("Fahrenheit");
        translation.setDesc("Lämpötila Fahrenheit-asteina");

        assertEquals(2, translation.getUnitId());
        assertEquals("fi", translation.getLang());
        assertEquals("Fahrenheit", translation.getName());
        assertEquals("Lämpötila Fahrenheit-asteina",
                translation.getDesc());
    }

    @Test
    void acceptsNullDescription() {
        UnitTranslation translation = new UnitTranslation(
                3,
                "en",
                "Kelvin",
                null
        );

        assertEquals("Kelvin", translation.getName());
        assertNull(translation.getDesc());
    }
}
