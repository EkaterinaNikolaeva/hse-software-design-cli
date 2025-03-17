package cli.commandexecutor;

import java.io.InputStream;
import java.io.OutputStream;

import cli.model.Command;
import cli.model.CommandResult;

public interface CommandExecutor {
    CommandResult execute(Command command, InputStream input, OutputStream output) throws Exception;
}
