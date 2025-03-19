package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
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

    @BeforeEach
    void setUp() throws IOException {
        wcExecutor = new WcExecutor();
        testFile = Files.createTempFile("testFile", ".txt");
    }

    @Test
    void testWcWithoutFlags() throws Exception {
        Files.writeString(testFile, "aba caba\nhello, world\n");
        CommandResult result = wcExecutor.execute(List.of(testFile.toString()), null, System.in, System.out);
        assertEquals(0, result.exitCode());
        String[] output = result.output().split(" ");
        assertEquals("2", output[0]);
        assertEquals("4", output[1]);
        assertEquals("22", output[2]);

        assertTrue(result.output().contains(testFile.toString()));
    }

    @Test
    void testWcWithFlagW() throws Exception {
        Files.writeString(testFile, "Hello world\nSome other text\n");
        Map<String, List<String>> wOption = new HashMap<>();
        wOption.put("w", null);
        CommandOptions options = new CommandOptions(wOption);
        CommandResult result = wcExecutor.execute(List.of(testFile.toString()), options, System.in, System.out);
        assertEquals(0, result.exitCode());
        assertTrue(result.output().contains("5"));
    }

    @Test
    void testWcNoFile() {
        WcExecutor executor = new WcExecutor();
        String text = "hello\nworld\n";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        CommandResult result = executor.execute(List.of(), null, inputStream, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("2 2 10\n", result.output());
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
        CommandResult result = executor.execute(List.of(testFile.toString(), secondFile.toString()), new CommandOptions(options), System.in, System.out);
        assertEquals(0, result.exitCode());
        String output = result.output();
        String[] lines = output.split(System.lineSeparator());
        assertEquals(3, lines.length);
        assertEquals("2 5 " + testFile, lines[0]);
        assertEquals("1 2 " + secondFile, lines[1]);
        assertEquals("3 7 total", lines[2]);
    }

}