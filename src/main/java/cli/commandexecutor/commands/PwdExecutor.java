package cli.commandexecutor.commands;

import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
import java.util.List;

/**
 * The PwdExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "pwd" command.
 * It returns the current working directory.
 */
public class PwdExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Get current work directory\n";
    private final static String FLAG_HELP_MESSAGE = "help";
    private final FileSystem fileSystem;

    public PwdExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Executes the "pwd" command.
     * If the "--help" option is specified, it returns a help message.
     * If any arguments are provided, an error message is returned.
     * Otherwise, it returns the current working directory.
     *
     * @param args          should be empty.
     * @param options       Command options.
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (!args.isEmpty()) {
            ioEnvironment.writeError("pwd does not have args" + System.lineSeparator());
            return 1;
        }
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("pwd: cannot write data to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }
        try {
            ioEnvironment.writeOutput(fileSystem.getCurrentWorkingDir() + System.lineSeparator());
        } catch (IOException e) {
            ioEnvironment.writeError("pwd: cannot write to output stream" + System.lineSeparator());
            return 1;
        }
        return 0;
    }
}
