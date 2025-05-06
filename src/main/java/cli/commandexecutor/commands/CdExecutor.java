package cli.commandexecutor.commands;

import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CdExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Change current working directory\n";
    private final static String FLAG_HELP_MESSAGE = "help";

    private final FileSystem fileSystem;

    public CdExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("cd: cannot write data to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }

        Path newCwd;
        if (args.isEmpty()) {
            newCwd = Path.of(System.getProperty("user.dir"));
        } else {
            newCwd = fileSystem.resolvePath(Path.of(args.getFirst()));
        }

        if (!Files.exists(newCwd)) {
            ioEnvironment.writeError("cd: directory doesn't exist (%s)%s".formatted(newCwd.toString(), System.lineSeparator()));
            return 1;
        }

        fileSystem.changeDir(newCwd);
        return 0;
    }
}
