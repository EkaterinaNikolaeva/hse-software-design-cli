package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.util.List;

public interface InternalCommandExecutor {
    CommandResult execute(List<String> args, CommandOptions options);
}
