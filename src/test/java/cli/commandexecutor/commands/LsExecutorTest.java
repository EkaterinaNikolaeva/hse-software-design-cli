package cli.commandexecutor.commands;

import cli.filesystem.FileSystemImpl;
import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LsExecutorTest {
    private LsExecutor lsExecutor;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private IOEnvironment ioEnvironment;
    private FileSystemImpl fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystemImpl();
        fileSystem.changeDir(
                Path.of("./src/test/resources/ls"
                )
        );
        lsExecutor = new LsExecutor(fileSystem);
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
    }

    @Test
    void testExecuteWithoutArgs() {
        int result = lsExecutor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertTrue(outputStream.toString().contains("a/"));
        assertTrue(outputStream.toString().contains("b/"));
        assertTrue(outputStream.toString().contains("1.txt"));
        assertTrue(outputStream.toString().contains("2.txt"));
    }

    @Test
    void testExecuteWithExistingDirectory() {
        int result = lsExecutor.execute(List.of("a"), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertTrue(outputStream.toString().contains("3.txt"));
    }

    @Test
    void testExecuteWithNonExistingDirectory() {
        int result = lsExecutor.execute(List.of("nonExistingDir"), new CommandOptions(), ioEnvironment);
        assertEquals(1, result);
        assertEquals("ls: cannot resolve path: nonExistingDir" + System.lineSeparator(), errorStream.toString());
    }

    @Test
    void testExecuteWithFile() {
        int result = lsExecutor.execute(List.of("1.txt"), new CommandOptions(), ioEnvironment);
        assertEquals(1, result);
        assertEquals("ls: cannot resolve path: 1.txt" + System.lineSeparator(), errorStream.toString());
    }

    @Test
    void testExecuteWithTooManyArguments() {
        int result = lsExecutor.execute(List.of("arg1", "arg2"), new CommandOptions(), ioEnvironment);
        assertEquals(1, result);
        assertEquals("ls can take arguments eq or less 1 arg" + System.lineSeparator(), errorStream.toString());
    }

}
