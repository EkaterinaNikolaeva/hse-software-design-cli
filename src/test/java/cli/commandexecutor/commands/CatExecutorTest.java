package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatExecutorTest {
    private CatExecutor catExecutor;
    private Path testFile;
    ByteArrayOutputStream outputStream;
    ByteArrayOutputStream errorStream;
    IOEnvironment ioEnvironment;

    @BeforeEach
    void setUp() throws IOException {
        catExecutor = new CatExecutor();
        testFile = Files.createTempFile("testFile", ".txt");
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
    }

    @Test
    void testExecuteValid() throws IOException {
        Files.writeString(testFile, "Hello, World!", StandardOpenOption.WRITE);
        int result = catExecutor.execute(List.of(testFile.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals("Hello, World!", outputStream.toString());
    }

    @Test
    void testExecuteMultilineFile() throws IOException {
        String content = "aba\ncaba\nhello!";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);
        int result = catExecutor.execute(List.of(testFile.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals(content, outputStream.toString());
    }

    @Test
    void testExecuteMultipleFiles() throws IOException {
        String content = "aba\ncaba\nhello!";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);
        Path secondFile = Files.createTempFile("secondFile", ".txt");
        String contentSecondFile = "some content\nother\n";
        Files.writeString(secondFile, contentSecondFile, StandardOpenOption.WRITE);
        int result = catExecutor.execute(List.of(testFile.toString(), secondFile.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals(content + contentSecondFile, outputStream.toString());
    }

    @Test
    void testExecuteWithNonExistentFile() {
        int result = catExecutor.execute(List.of("nonexistent.txt"), new CommandOptions(), ioEnvironment);
        assertEquals(1, result);
        assertEquals("cat: cannot read file nonexistent.txt", errorStream.toString());
    }

    @Test
    void testExecuteWithNoArguments() {
        String text = "hello\nworld\n";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        ioEnvironment = new IOEnvironmentImpl(inputStream, outputStream, System.err);
        int result = catExecutor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals("hello\nworld\n", outputStream.toString());
    }

    @Test
    void testExecuteWithHelpFlag() {
        Map<String, List<String>> helpOptions = new HashMap<>();
        helpOptions.put("help", null);
        int result = catExecutor.execute(Collections.emptyList(), new CommandOptions(helpOptions), ioEnvironment);
        assertEquals(0, result);
        assertEquals("Get files' content\n", outputStream.toString());
    }
}
