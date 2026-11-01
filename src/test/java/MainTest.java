import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMainConstructor() {
        Main main = new Main();

        assertNotNull(main);
    }

    @Test
    void testInit() {
        Main main = new Main();

        assertDoesNotThrow(main::init);
    }
}