package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrepExecutorTest {
    private GrepExecutor grepExecutor;
    private Path testFile;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private IOEnvironment ioEnvironment;

    @BeforeEach
    void setUp() throws IOException {
        grepExecutor = new GrepExecutor();
        testFile = Files.createTempFile("testFile", ".txt");
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
    }

    @Test
    void testBasicPatternMatching() throws IOException {
        String content = "hello world\ngoodbye world\napple banana";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        int result = grepExecutor.execute(
                List.of("world", testFile.toString()),
                new CommandOptions(),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("hello world\ngoodbye world\n", outputStream.toString());
    }

    @Test
    void testCaseInsensitiveMatching() throws IOException {
        String content = "Hello World\nhello world\nHELLO WORLD";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        Map<String, List<String>> options = new HashMap<>();
        options.put("i", null);

        int result = grepExecutor.execute(
                List.of("hello", testFile.toString()),
                new CommandOptions(options),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("Hello World\nhello world\nHELLO WORLD\n", outputStream.toString());
    }

    @Test
    void testWholeWordMatching() throws IOException {
        String content = "hello\n hello \nhello \n hello world\nhello world\nworld hello\n world hello \nhelloworld\nworldhello";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        Map<String, List<String>> options = new HashMap<>();
        options.put("w", null);

        int result = grepExecutor.execute(
                List.of("hello", testFile.toString()),
                new CommandOptions(options),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("hello\n hello \nhello \n hello world\nhello world\nworld hello\n world hello \n", outputStream.toString());
    }

    @Test
    void testAfterContextOption() throws IOException {
        String content = "line1\nline2\nmatch1\nline3\nline4\nmatch2\nline5";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        Map<String, List<String>> options = new HashMap<>();
        options.put("A", null);

        int result = grepExecutor.execute(
                List.of("2", "match", testFile.toString()),
                new CommandOptions(options),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("match1\nline3\nline4\nmatch2\nline5\n", outputStream.toString());
    }

    @Test
    void testInvalidAfterContextValue() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("A", null);

        int result = grepExecutor.execute(
                List.of("not_a_number", "pattern", "file.txt"),
                new CommandOptions(options),
                ioEnvironment
        );

        assertEquals(1, result);
        assertTrue(errorStream.toString().contains("Invalid number for -A option"));
    }

    @Test
    void testFileNotFound() {
        int result = grepExecutor.execute(
                List.of("pattern", "nonexistent.txt"),
                new CommandOptions(),
                ioEnvironment
        );

        assertEquals(1, result);
        assertTrue(errorStream.toString().contains("Error reading file"));
    }

    @Test
    void testInsufficientArguments() {
        int result = grepExecutor.execute(
                List.of("single_argument"),
                new CommandOptions(),
                ioEnvironment
        );

        assertEquals(1, result);
        assertEquals("Usage: grep [options] <pattern> <file>", errorStream.toString().trim());
    }

    @Test
    void testComplexPatternMatching() throws IOException {
        String content = "Паспорт: 11 22 345678\nНе паспорт: 123 45 6789\nЕщё паспорт: 99 88 123456\nЕщё не паспорт: 12 34 67890";

        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        int result = grepExecutor.execute(
                List.of("\\d{2} \\d{2} \\d{6}", testFile.toString()),
                new CommandOptions(),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("Паспорт: 11 22 345678\nЕщё паспорт: 99 88 123456\n", outputStream.toString());
    }

    @Test
    void testMultipleMatchesInLine() throws IOException {
        String content = "hello hello hello\nsingle\nhello world hello";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);

        int result = grepExecutor.execute(
                List.of("hello", testFile.toString()),
                new CommandOptions(),
                ioEnvironment
        );

        assertEquals(0, result);
        assertEquals("hello hello hello\nhello world hello\n", outputStream.toString());
    }
}