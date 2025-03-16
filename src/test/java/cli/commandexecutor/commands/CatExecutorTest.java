package cli.commandexecutor.commands;

import cli.model.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

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
        CommandResult result = catExecutor.execute(List.of(testFile.toString()), Collections.emptyList());
        assertEquals(0, result.exitCode());
        assertEquals("Hello, World!", result.output());
    }

    @Test
    void testExecuteMultilineFile() throws IOException {
        String content = "aba\ncaba\nhello!";
        Files.writeString(testFile, content, StandardOpenOption.WRITE);
        CommandResult result = catExecutor.execute(List.of(testFile.toString()), Collections.emptyList());
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
        CommandResult result = catExecutor.execute(List.of(testFile.toString(), secondFile.toString()), Collections.emptyList());
        assertEquals(0, result.exitCode());
        assertEquals(content + contentSecondFile, result.output());
    }

    @Test
    void testExecuteWithNonExistentFile() {
        CommandResult result = catExecutor.execute(List.of("nonexistent.txt"), Collections.emptyList());
        assertEquals(1, result.exitCode());
        assertEquals("cat: cannot read file nonexistent.txt", result.output());
    }

    @Test
    void testExecuteWithNoArguments() {
        CommandResult result = catExecutor.execute(Collections.emptyList(), Collections.emptyList());
        assertEquals(1, result.exitCode());
        assertEquals("cat: files are not specified", result.output());
    }
}
