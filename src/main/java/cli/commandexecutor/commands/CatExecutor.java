package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.List;

public class CatExecutor implements InternalCommandExecutor {
    private final static String helpMessage = "Get files' content";
    private final static String flagHelpMessage = "help";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options) {
        if (options != null && options.containsOption(flagHelpMessage)) {
            return new CommandResult(0, helpMessage);
        }
        if (args.isEmpty()) {
            return new CommandResult(1, "cat: files are not specified");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String file : args) {
            Path filePath = Path.of(file);
            try {
                stringBuilder.append(Files.readString(filePath));
            } catch (IOException e) {
                return new CommandResult(1, "cat: cannot read file " + file);
            }
        }
        return new CommandResult(0, stringBuilder.toString());
    }
}
