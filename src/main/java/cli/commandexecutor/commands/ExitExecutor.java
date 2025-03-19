package cli.commandexecutor.commands;

import cli.exceptions.ExitCommandException;
import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExitExecutor implements InternalCommandExecutor {
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        throw new ExitCommandException();
    }
}
