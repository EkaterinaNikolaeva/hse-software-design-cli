package cli.commandexecutor.commands;

import cli.model.CommandResult;
import cli.model.CommandOptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The WcExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "wc" command.
 * It counts and prints the number of lines, words, and bytes in files or input streams.
 */
public class WcExecutor implements InternalCommandExecutor {
    private static final String HELP_MESSAGE = "Print lines, words and bytes in file.\n";
    private static final String FLAG_HELP = "help";
    private static final String FLAG_LINES = "l";
    private static final String FLAG_WORDS = "w";
    private static final String FLAG_BYTES = "c";

    private void printStatistics(CommandOptions options, @org.jetbrains.annotations.NotNull StringBuilder output,
                                 String file, int lines, int words, int bytes) {
        if (options == null || options.containsOption(FLAG_LINES)) {
            output.append(lines).append(" ");
        }
        if (options == null || options.containsOption(FLAG_WORDS)) {
            output.append(words).append(" ");
        }
        if (options == null || options.containsOption(FLAG_BYTES)) {
            output.append(bytes).append(" ");
        }
        if (file != null) {
            output.append(file);
        } else {
            output.delete(output.length() - 1, output.length());
        }
        output.append(System.lineSeparator());
    }

    @Contract("_, _, _ -> new")
    private @NotNull CommandResult processInputStream(CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        StringBuilder output = new StringBuilder();
        int lines = 0;
        int words = 0;
        int bytes = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                words += line.split("\\s+").length;
                bytes += line.getBytes().length;
            }
        } catch (IOException e) {
            return new CommandResult(1, "wc: error reading input stream");
        }
        printStatistics(options, output, null, lines, words, bytes);
        try {
            outputStream.write(output.toString().getBytes());
        } catch (IOException e) {
            return new CommandResult(1, "wc: error writing output stream");
        }
        return new CommandResult(0, output.toString());
    }

    /**
     * Executes the "wc" command.
     * If the "--help" option is specified, it returns a help message.
     * If no file arguments are provided, it processes the input stream.
     * Otherwise, it reads and counts statistics for each specified file.
     * -l - lines number.
     * -w - words number.
     * -c - bytes number.
     * By default, all these flags are used.
     *
     * @param args         List of file names to be analyzed.
     * @param options      Command options specifying which statistics to include.
     * @param inputStream  Input stream used when no files are specified.
     * @param outputStream Output stream to write the command result.
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public CommandResult execute(List<String> args, CommandOptions options, InputStream inputStream, OutputStream outputStream) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            try {
                outputStream.write(HELP_MESSAGE.getBytes());
            } catch (IOException e) {
                return new CommandResult(1, "wc: cannot write data to output stream");
            }
            return new CommandResult(0, HELP_MESSAGE);
        }
        if (args.isEmpty()) {
            return processInputStream(options, inputStream, outputStream);
        }
        int totalLines = 0;
        int totalWords = 0;
        int totalBytes = 0;
        StringBuilder output = new StringBuilder();
        for (String file : args) {
            Path filePath = Path.of(file);
            try {
                String content = Files.readString(filePath);
                int lines = content.split("\n").length;
                int words = content.split("\\s+").length;
                int bytes = content.getBytes().length;
                totalLines += lines;
                totalWords += words;
                totalBytes += bytes;
                printStatistics(options, output, file, lines, words, bytes);
            } catch (IOException e) {
                return new CommandResult(1, "wc: cannot read file " + file);
            }
        }
        if (args.size() > 1) {
            printStatistics(options, output, "total", totalLines, totalWords, totalBytes);
        }
        return new CommandResult(0, output.toString().trim());
    }
}
