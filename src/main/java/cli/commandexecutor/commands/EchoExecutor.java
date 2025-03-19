package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The EchoExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "echo" command.
 * It displays a line of text.
 */
public class EchoExecutor implements InternalCommandExecutor {
    private static final String HELP_MESSAGE = "Display a line of text.\n";
    private static final String FLAG_HELP = "help";
    private static final String FLAG_NO_NEWLINE = "n";

    /**
     * Executes the "echo" command.
     * If no args are provided, it reads file from the input stream.
     * Else it displays lines provided.
     * If the "--help" option is specified, it returns a help message.
     * If the "-n" option is specified, it does not write new line at the end of text.
     *
     * @param args         Words to be written.
     * @param options      Command flags.
     * @param inputStream  Input stream for reading file when no words are specified.
     * @param outputStream Output stream for writing the command result.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            try {
                outputStream.write(HELP_MESSAGE.getBytes());
            } catch (IOException e) {
                return new CommandResult(1, "echo: cannot write data to output stream");
            }
            return new CommandResult(0, HELP_MESSAGE);
        }
        String output = String.join(" ", args);
        try {
            outputStream.write((output + System.lineSeparator()).getBytes() );
        } catch (IOException e) {
            return new CommandResult(1, "echo: cannot write to output stream");
        }
        if (options != null && options.containsOption(FLAG_NO_NEWLINE)) {
            return new CommandResult(0, output);
        }
        return new CommandResult(0, output + System.lineSeparator());
    }
}
