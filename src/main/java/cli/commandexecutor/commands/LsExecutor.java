package cli.commandexecutor.commands;

import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The LsExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "ls" command.
 * It lists files and directories in the current working directory.
 */
public class LsExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "List files and directories in the current working directory.\n";
    private final static String FLAG_HELP_MESSAGE = "help";
    private final FileSystem fileSystem;

    public LsExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Executes the "ls" command.
     * If the "--help" option is specified, it returns a help message.
     * If any arguments are provided, an error message is returned.
     * Otherwise, it lists the contents of the current working directory.
     *
     * @param args          should be empty.
     * @param options       Command options.
     * @param ioEnvironment input, output and error streams.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (!args.isEmpty()) {
            ioEnvironment.writeError("ls does not take arguments" + System.lineSeparator());
            return 1;
        }
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("ls: cannot write data to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }

        try {
            File[] files = fileSystem.getCurrentWorkingDir().toFile().listFiles();

            if (files == null) {
                ioEnvironment.writeError("ls: cannot access the directory" + System.lineSeparator());
                return 1;
            }

            StringBuilder output = new StringBuilder();
            for (File file : files) {
                output.append(file.getName()).append(file.isDirectory() ? "/" : "").append(System.lineSeparator());
            }

            ioEnvironment.writeOutput(output + System.lineSeparator());
        } catch (IOException e) {
            ioEnvironment.writeError("ls: cannot write to output stream" + System.lineSeparator());
            return 1;
        }
        return 0;
    }
}
