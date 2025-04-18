package cli.commandexecutor;

import java.io.InputStream;
import java.io.OutputStream;

import cli.exceptions.ExitCommandException;
import cli.model.Command;

/**
 * The CommandExecutor interface defines the contract for executing commands.
 * Implementations of this interface are responsible for executing a command,
 * handling input and output streams, and returning the result of the execution.
 */
public interface CommandExecutor {
    int execute(Command command, InputStream input, OutputStream output, OutputStream error) throws ExitCommandException;
}
