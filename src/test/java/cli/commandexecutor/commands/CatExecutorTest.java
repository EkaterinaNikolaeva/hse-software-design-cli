package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;
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

    @BeforeEach
    void setUp() throws IOException {
        catExecutor = new CatExecutor();
        testFile = Files.createTempFile("testFile", ".txt");
    }

    @Test
    void testExecuteValid() throws IOException {
        Files.writeString(testFile, "Hello, World!", StandardOpenOption.WRITE);
        CommandResult result = catExecutor.execute(List.of(testFile.toString()), new CommandOptions(), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("Hello, World!", result.output());
    }

    @Test
    void testExecuteMultilineFile() throws IOException {
        String content = "aba\ncaba\nhello!";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);
        CommandResult result = catExecutor.execute(List.of(testFile.toString()), new CommandOptions(), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals(content, result.output());
    }

    @Test
    void testExecuteMultipleFiles() throws IOException {
        String content = "aba\ncaba\nhello!";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);
        Path secondFile = Files.createTempFile("secondFile", ".txt");
        String contentSecondFile = "some content\nother\n";
        Files.writeString(secondFile, contentSecondFile, StandardOpenOption.WRITE);
        CommandResult result = catExecutor.execute(List.of(testFile.toString(), secondFile.toString()), new CommandOptions(), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals(content + contentSecondFile, result.output());
    }

    @Test
    void testExecuteWithNonExistentFile() {
        CommandResult result = catExecutor.execute(List.of("nonexistent.txt"), new CommandOptions(), System.in, System.out);
        assertEquals(1, result.exitCode());
        assertEquals("cat: cannot read file nonexistent.txt", result.output());
    }

    @Test
    void testExecuteWithNoArguments() {
        String text = "hello\nworld\n";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        CommandResult result = catExecutor.execute(Collections.emptyList(), new CommandOptions(), inputStream, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("hello\nworld\n", result.output());
    }

    @Test
    void testExecuteWithHelpFlag() {
        Map<String, List<String>> helpOptions = new HashMap<>();
        helpOptions.put("help", null);
        CommandResult result = catExecutor.execute(Collections.emptyList(), new CommandOptions(helpOptions), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("Get files' content\n", result.output());
    }
}
