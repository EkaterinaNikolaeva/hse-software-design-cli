package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.util.List;

public class ExitExecutor implements InternalCommandExecutor {
    @Override
    public CommandResult execute(List<String> args, CommandOptions options) {
        return null;
    }
}
