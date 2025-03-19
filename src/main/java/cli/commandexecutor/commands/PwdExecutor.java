package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PwdExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Get current work directory";
    private final static String FLAG_HELP_MESSAGE = "help";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream)  {
        if (!args.isEmpty()) {
            return new CommandResult(1, "pwd does not have args");
        }
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
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
