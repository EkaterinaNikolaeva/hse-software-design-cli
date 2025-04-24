package cli.commandexecutor.commands;

import cli.environment.Environment;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetEnvironmentExecutor implements InternalCommandExecutor {
    private final Environment environment;

    public SetEnvironmentExecutor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int execute(@NotNull List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (args.size() != 2) {
            ioEnvironment.writeError("incorrect set variable statement" + System.lineSeparator());
            return 1;
        }
        environment.setVariable(args.get(0), args.get(1));
        return 0;
    }
}
