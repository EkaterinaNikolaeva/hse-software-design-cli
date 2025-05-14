package cli.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemImplTest {
    private FileSystemImpl fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new FileSystemImpl();
    }

    @Test
    void testGetCurrentWorkingDir() {
        Path expectedCwd = Path.of(System.getProperty("user.dir"));
        assertEquals(expectedCwd, fileSystem.getCurrentWorkingDir(), "Current working directory should match the system property.");
    }

    @Test
    void testResolvePathWithRelativePath() {
        Path relativePath = Path.of("relative/path");
        Path expectedResolvedPath = fileSystem.getCurrentWorkingDir().resolve(relativePath).normalize();
        assertEquals(expectedResolvedPath, fileSystem.resolvePath(relativePath), "Relative paths should be resolved against the current working directory.");
    }

    @Test
    void testChangeDir() {
        Path newDir = Path.of("new/directory");
        Path resolvedPath = fileSystem.getCurrentWorkingDir().resolve(newDir).normalize();

        fileSystem.changeDir(newDir);
        assertEquals(resolvedPath, fileSystem.getCurrentWorkingDir(), "The current working directory should change to the resolved path.");
    }
}