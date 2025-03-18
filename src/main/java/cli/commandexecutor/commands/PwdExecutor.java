package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.util.List;

public class PwdExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Get current work directory";
    private final static String FLAG_HELP_MESSAGE = "help";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options)  {
        if (!args.isEmpty()) {
            return new CommandResult(1, "pwd does not have args");
        }
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            return new CommandResult(0, HELP_MESSAGE);
        }
        String curDir = System.getProperty("user.dir");
        return new CommandResult(0, curDir);
    }
}
