package cli.commandexecutor.commands;

import cli.model.CommandResult;

import java.util.List;

public class EchoExecutor implements InternalCommandExecutor {
    @Override
    public CommandResult execute(List<String> args, List<String> flags) {
        return null;
    }
}
