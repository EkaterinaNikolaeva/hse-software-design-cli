package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The PwdExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "pwd" command.
 * It returns the current working directory.
 */
public class PwdExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Get current work directory\n";
    private final static String FLAG_HELP_MESSAGE = "help";

    /**
     * Executes the "pwd" command.
     * If the "--help" option is specified, it returns a help message.
     * If any arguments are provided, an error message is returned.
     * Otherwise, it returns the current working directory.
     *
     * @param args         should be empty.
     * @param options      Command options.
     * @param inputStream  Input stream, not used.
     * @param outputStream Output stream for writing the command result.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream)  {
        if (!args.isEmpty()) {
            return new CommandResult(1, "pwd does not have args");
        }
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                outputStream.write(HELP_MESSAGE.getBytes());
            } catch (IOException e) {
                return new CommandResult(1, "pwd: cannot write data to output stream");
            }
            return new CommandResult(0, HELP_MESSAGE);
        }
        String curDir = System.getProperty("user.dir");
        try {
            outputStream.write((curDir + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            return new CommandResult(1, "pwd: cannot write to output stream");
        }
        return new CommandResult(0, curDir);
    }
}
