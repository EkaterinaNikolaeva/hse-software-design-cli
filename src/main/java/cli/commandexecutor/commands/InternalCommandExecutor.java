package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The InternalCommandExecutor interface defines the contract for executing internal commands.
 * Implementations of this interface are responsible for executing a specific internal command
 * and managing the input/output streams associated with it.
 */
public interface InternalCommandExecutor {
    CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream);
}
