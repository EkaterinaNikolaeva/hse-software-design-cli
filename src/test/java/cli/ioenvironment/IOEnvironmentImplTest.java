package cli.ioenvironment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IOEnvironmentImplTest {
    private ByteArrayInputStream testInputStream;
    private ByteArrayOutputStream testOutputStream;
    private ByteArrayOutputStream testErrorStream;
    private IOEnvironmentImpl ioEnvironment;

    @BeforeEach
    void setUp() {
        testInputStream = new ByteArrayInputStream("Hello, world!".getBytes());
        testOutputStream = new ByteArrayOutputStream();
        testErrorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(testInputStream, testOutputStream, testErrorStream);
    }


    @Test
    void testWriteError() {
        String error = "Error message";
        ioEnvironment.writeError(error);
        assertEquals(error, testErrorStream.toString());
    }

    @Test
    void testWriteOutput() throws IOException {
        String output = "Test output";
        ioEnvironment.writeOutput(output);
        assertEquals(output, testOutputStream.toString());
    }

    @Test
    void testRead() throws IOException {
        String expected = "Hello, world!";
        String result = ioEnvironment.read();
        assertEquals(expected, result);
    }

    @Test
    void testWriteOutputEmptyString() throws IOException {
        ioEnvironment.writeOutput("");
        assertEquals("", testOutputStream.toString());
    }

    @Test
    void testWriteErrorEmptyString() {
        ioEnvironment.writeError("");
        assertEquals("", testErrorStream.toString());
    }
}
