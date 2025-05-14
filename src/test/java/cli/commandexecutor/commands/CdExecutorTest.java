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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CdExecutorTest {
    private CdExecutor cdExecutor;
    private FileSystemImpl fileSystem;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private IOEnvironment ioEnvironment;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        fileSystem = new FileSystemImpl();
        cdExecutor = new CdExecutor(fileSystem);
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
        tempDir = Files.createTempDirectory("cdTestDir");
    }

    @Test
    void testExecuteWithoutArgsChangesToUserDir() {
        int result = cdExecutor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals(System.getProperty("user.dir"), fileSystem.getCurrentWorkingDir().toString());
    }

    @Test
    void testExecuteToValidDirectory() {
        int result = cdExecutor.execute(List.of(tempDir.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals(tempDir.toAbsolutePath().normalize(), fileSystem.getCurrentWorkingDir());
    }

    @Test
    void testExecuteToNonExistentDirectory() {
        String nonExistent = tempDir.resolve("doesNotExist").toString();
        int result = cdExecutor.execute(List.of(nonExistent), new CommandOptions(), ioEnvironment);
        assertNotEquals(0, result);
    }

    @Test
    void testExecuteToFile() throws IOException {
        var file = Files.createFile(Path.of(tempDir.toAbsolutePath() + File.pathSeparator+ "1.txt"));
        int result = cdExecutor.execute(List.of(file.normalize().toString()), new CommandOptions(), ioEnvironment);
        assertNotEquals(0, result);
    }

    @Test
    void testCdThenPwdMatch() {
        int cdResult = cdExecutor.execute(List.of(tempDir.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, cdResult);

        PwdExecutor pwdExecutor = new PwdExecutor(fileSystem);
        ByteArrayOutputStream pwdOutputStream = new ByteArrayOutputStream();
        IOEnvironment pwdEnvironment = new IOEnvironmentImpl(System.in, pwdOutputStream, errorStream);

        int pwdResult = pwdExecutor.execute(Collections.emptyList(), new CommandOptions(), pwdEnvironment);
        assertEquals(0, pwdResult);

        assertEquals(tempDir.toAbsolutePath().normalize() + System.lineSeparator(), pwdOutputStream.toString());
    }
}
