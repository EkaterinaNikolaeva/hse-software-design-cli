package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.List;

/**
 * The CatExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "cat" command.
 * It reads and outputs file contents or standard input data.
 */
public class CatExecutor implements InternalCommandExecutor {
    private final static String HELP_MESSAGE = "Get files' content\n";
    private final static String FLAG_HELP_MESSAGE = "help";

    /**
     * Executes the "cat" command.
     * If no args are provided, it reads from the input stream.
     * Else it reads and returns the content of the files provided.
     * If the "help" option is specified, it returns a help message.
     *
     * @param args         List of file names to be read.
     * @param options      Command flags.
     * @param inputStream  Input stream for reading data when no files are specified.
     * @param outputStream Output stream for writing the command result.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                outputStream.write(HELP_MESSAGE.getBytes());
            } catch (IOException e) {
                return new CommandResult(1, "cat: cannot write data to output stream");
            }
            return new CommandResult(0, HELP_MESSAGE);
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
            return new CommandResult(1, "cat: cannot write data to output stream");
        }
        return new CommandResult(0, stringBuilder.toString());
    }
}
