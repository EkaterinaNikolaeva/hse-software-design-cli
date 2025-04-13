package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WcExecutorTest {
    private WcExecutor wcExecutor;
    private Path testFile;
    ByteArrayOutputStream outputStream;
    ByteArrayOutputStream errorStream;
    IOEnvironment ioEnvironment;

    @BeforeEach
    void setUp() throws IOException {
        wcExecutor = new WcExecutor();
        testFile = Files.createTempFile("testFile", ".txt");
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
    }

    @Test
    void testWcWithoutFlags() throws Exception {
        Files.writeString(testFile, "aba caba\nhello, world\n");
        int result = wcExecutor.execute(List.of(testFile.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        String[] output = outputStream.toString().split(" ");
        assertEquals("2", output[0]);
        assertEquals("4", output[1]);
        assertEquals("22", output[2]);

        assertTrue(outputStream.toString().contains(testFile.toString()));
    }

    @Test
    void testWcWithFlagW() throws Exception {
        Files.writeString(testFile, "Hello world\nSome other text\n");
        Map<String, List<String>> wOption = new HashMap<>();
        wOption.put("w", null);
        CommandOptions options = new CommandOptions(wOption);
        int result = wcExecutor.execute(List.of(testFile.toString()), options, ioEnvironment);
        assertEquals(0, result);
        assertTrue(outputStream.toString().contains("5"));
    }

    @Test
    void testWcNoFile() {
        WcExecutor executor = new WcExecutor();
        String text = "hello\nworld\n";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        ioEnvironment = new IOEnvironmentImpl(inputStream, outputStream, errorStream);
        int result = executor.execute(List.of(), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals("2 2 10\n", outputStream.toString());
    }

    @Test
    void testWcSeveralFiles() throws IOException {
        Path secondFile = Files.createTempFile("testFile", ".txt");
        Files.writeString(testFile, "Hello world\nSome other text\n");
        Files.writeString(secondFile, "aba caba\n");
        Map<String, List<String>> options = new HashMap<>();
        options.put("w", null);
        options.put("l", null);
        WcExecutor executor = new WcExecutor();
        int result = executor.execute(List.of(testFile.toString(), secondFile.toString()), new CommandOptions(options), ioEnvironment);
        assertEquals(0, result);
        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());
        assertEquals(3, lines.length);
        assertEquals("2 5 " + testFile, lines[0]);
        assertEquals("1 2 " + secondFile, lines[1]);
        assertEquals("3 7 total", lines[2]);
    }
}
