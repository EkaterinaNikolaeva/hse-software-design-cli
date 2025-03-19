package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.List;

public class CatExecutor implements InternalCommandExecutor {
    private final static String helpMessage = "Get files' content";
    private final static String flagHelpMessage = "help";

    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        if (options != null && options.containsOption(flagHelpMessage)) {
            return new CommandResult(0, helpMessage);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (args.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException e) {
                return new CommandResult(1, "cat: error reading input stream");
            }
        } else {
            for (String file : args) {
                Path filePath = Path.of(file);
                try {
                    stringBuilder.append(Files.readString(filePath));
                } catch (IOException e) {
                    return new CommandResult(1, "cat: cannot read file " + file);
                }
            }
        }
        try {
            outputStream.write(stringBuilder.toString().getBytes());
        } catch (IOException e) {
            return new CommandResult(1, "cannot write data to output stream");
        }
        return new CommandResult(0, stringBuilder.toString());
    }
}
