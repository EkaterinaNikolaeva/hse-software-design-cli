package cli.commandexecutor.commands;

import cli.exceptions.ExitCommandException;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

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
     * @param args          should be empty.
     * @param options       should be empty.
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        throw new ExitCommandException();
    }
}
