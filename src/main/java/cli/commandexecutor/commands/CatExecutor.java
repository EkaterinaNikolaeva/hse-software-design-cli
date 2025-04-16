package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

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
     * @param args          List of file names to be read.
     * @param options       Command flags.
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (options != null && options.containsOption(FLAG_HELP_MESSAGE)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("cat: cannot write data to output stream" + System.lineSeparator());
                return 1;
            }
            return 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (args.isEmpty()) {
            try {
                String text = ioEnvironment.read();
                ioEnvironment.writeOutput(text);
                return 0;
            } catch (IOException e) {
                ioEnvironment.writeError("cat: error reading input stream" + System.lineSeparator());
                return 1;
            }
        }
        for (String file : args) {
            Path filePath = Path.of(file);
            try {
                stringBuilder.append(Files.readString(filePath));
            } catch (IOException e) {
                ioEnvironment.writeError("cat: cannot read file " + file + System.lineSeparator());
                return 1;
            }
        }
        try {
            ioEnvironment.writeOutput(stringBuilder.toString());
        } catch (IOException e) {
            ioEnvironment.writeError("cat: cannot write data to output stream" + System.lineSeparator());
            return 1;
        }
        return 0;
    }
}
