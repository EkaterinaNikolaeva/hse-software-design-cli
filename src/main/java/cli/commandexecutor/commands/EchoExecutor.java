package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class EchoExecutor implements InternalCommandExecutor {
    private static final String HELP_MESSAGE = "Display a line of text.";
    private static final String FLAG_HELP = "help";
    private static final String FLAG_NO_NEWLINE = "n";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            return new CommandResult(0, HELP_MESSAGE);
        }
        String output = String.join(" ", args);
        try {
            outputStream.write(output.getBytes());
        } catch (IOException e) {
            return new CommandResult(1, "echo: cannot write to output stream");
        }
        if (options != null && options.containsOption(FLAG_NO_NEWLINE)) {
            return new CommandResult(0, output);
        }
        return new CommandResult(0, output + System.lineSeparator());
    }
}
