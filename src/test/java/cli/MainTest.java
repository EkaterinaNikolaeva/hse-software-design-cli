package cli;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testHelloWorld() {
        String expected = "HelloWorld";
        String actual = Main.HelloWorld();
        assertEquals(expected, actual);
    }
}
