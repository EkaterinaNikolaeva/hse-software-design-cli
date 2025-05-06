package cli.commandexecutor.commands;

import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.util.List;

public class CdExecutor implements InternalCommandExecutor{
    private final FileSystem fileSystem;

    public CdExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        return 0;
    }
}
