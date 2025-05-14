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

    @Test
    void testCdThenCatWorksInNewDir() throws IOException {
        Path testFile = Files.createFile(tempDir.resolve("testFile.txt"));
        Files.writeString(testFile, "Hello, world!");

        int cdResult = cdExecutor.execute(List.of(tempDir.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, cdResult);

        CatExecutor catExecutor = new CatExecutor(fileSystem);
        ByteArrayOutputStream catOutputStream = new ByteArrayOutputStream();
        IOEnvironment catEnvironment = new IOEnvironmentImpl(System.in, catOutputStream, errorStream);

        int catResult = catExecutor.execute(List.of("testFile.txt"), new CommandOptions(), catEnvironment);
        assertEquals(0, catResult);

        assertEquals("Hello, world!", catOutputStream.toString());
    }

    @Test
    void testCdThenGrepWorksInNewDir() throws IOException {
        Path testFile = Files.createFile(tempDir.resolve("testFile.txt"));
        Files.writeString(testFile, "Hello, world!\nHello, Java!\n");

        int cdResult = cdExecutor.execute(List.of(tempDir.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, cdResult);

        GrepExecutor grepExecutor = new GrepExecutor(fileSystem);
        ByteArrayOutputStream grepOutputStream = new ByteArrayOutputStream();
        IOEnvironment grepEnvironment = new IOEnvironmentImpl(System.in, grepOutputStream, errorStream);

        int grepResult = grepExecutor.execute(List.of("Hello, Java!", "testFile.txt"), new CommandOptions(), grepEnvironment);
        assertEquals(0, grepResult);

        assertEquals("Hello, Java!" + System.lineSeparator(), grepOutputStream.toString());
    }

    @Test
    void testCdThenWcWorksInNewDir() throws IOException {
        Path testFile = Files.createFile(tempDir.resolve("testFile.txt"));
        Files.writeString(testFile, "Hello, world!Hello, Java!");

        int cdResult = cdExecutor.execute(List.of(tempDir.toString()), new CommandOptions(), ioEnvironment);
        assertEquals(0, cdResult);

        WcExecutor wcExecutor = new WcExecutor(fileSystem);
        ByteArrayOutputStream wcOutputStream = new ByteArrayOutputStream();
        IOEnvironment wcEnvironment = new IOEnvironmentImpl(System.in, wcOutputStream, errorStream);

        int wcResult = wcExecutor.execute(List.of("testFile.txt"), new CommandOptions(), wcEnvironment);
        assertEquals(0, wcResult);

        assertEquals("1 3 25 testFile.txt" + System.lineSeparator(), wcOutputStream.toString());
    }
}
