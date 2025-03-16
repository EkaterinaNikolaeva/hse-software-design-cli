package cli.commandexecutor;

import cli.model.Command;
import cli.model.CommandResult;

public interface CommandExecutor {
    CommandResult execute(Command command);
}
