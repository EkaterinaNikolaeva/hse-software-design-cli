package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.util.List;

public class PwdExecutor implements InternalCommandExecutor {
    private final static String helpMessage = "Get current work directory";
    private final static String flagHelpMessage = "help";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options)  {
        if (!args.isEmpty()) {
            return new CommandResult(1, "pwd does not have args");
        }
        if (options.containsOption(flagHelpMessage)) {
            return new CommandResult(0, helpMessage);
        }
        String curDir = System.getProperty("user.dir");
        return new CommandResult(0, curDir);
    }
}
