package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.util.List;

/**
 * The InternalCommandExecutor interface defines the contract for executing internal commands.
 * Implementations of this interface are responsible for executing a specific internal command
 * and managing the input/output streams associated with it.
 */
public interface InternalCommandExecutor {
    int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment);
}