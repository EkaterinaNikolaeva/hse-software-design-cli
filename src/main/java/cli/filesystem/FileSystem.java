package cli.filesystem;

import java.nio.file.Path;

public interface FileSystem {
    Path getCurrentWorkingDir();
    Path resolvePath(Path path);
    void changeDir(Path path);
}
