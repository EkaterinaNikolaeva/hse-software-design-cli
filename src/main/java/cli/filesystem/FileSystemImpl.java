package cli.filesystem;

import java.nio.file.Path;

public class FileSystemImpl implements FileSystem {
    private Path cwd = Path.of(System.getProperty("user.dir"));

    public FileSystemImpl() {}

    @Override
    public Path getCurrentWorkingDir() {
        return cwd;
    }

    @Override
    public Path resolvePath(Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return cwd.resolve(path).normalize();
    }

    @Override
    public void changeDir(Path path) {
        cwd = resolvePath(path);
    }
}
