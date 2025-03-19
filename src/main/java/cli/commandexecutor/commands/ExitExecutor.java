package cli.commandexecutor.commands;

import cli.exceptions.ExitCommandException;
import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * The ExitExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "exit" command.
 * It causes normal process termination.
 */
public class ExitExecutor implements InternalCommandExecutor {
    /**
     * Executes the "exit" command.
     *
     * @param args         should be empty.
     * @param options      should be empty.
     * @param inputStream  Input stream, not used.
     * @param outputStream Output stream, not used.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        throw new ExitCommandException();
    }
}
