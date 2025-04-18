package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
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
     * @param args          Words to be written.
     * @param options       Command flags.
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("echo: cannot write data to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }
        String output = String.join(" ", args);
        if (options != null && options.containsOption(FLAG_NO_NEWLINE)) {
            try {
                ioEnvironment.writeOutput(output);
            } catch (IOException e) {
                ioEnvironment.writeError("echo: cannot write to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }
        try {
            ioEnvironment.writeOutput(output + System.lineSeparator());
        } catch (IOException e) {
            ioEnvironment.writeError("echo: cannot write to output stream" + System.lineSeparator());
            return 1;
        }
        return 0;
    }
}
