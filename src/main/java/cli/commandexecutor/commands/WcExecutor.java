package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

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

    private void printStatistics(CommandOptions options, @org.jetbrains.annotations.NotNull StringBuilder output, String file, int lines, int words, int bytes) {
        if (options.isEmpty() || options.containsOption(FLAG_LINES)) {
            output.append(lines).append(" ");
        }
        if (options.isEmpty() || options.containsOption(FLAG_WORDS)) {
            output.append(words).append(" ");
        }
        if (options.isEmpty() || options.containsOption(FLAG_BYTES)) {
            output.append(bytes).append(" ");
        }
        if (file != null) {
            output.append(file);
        } else {
            output.delete(output.length() - 1, output.length());
        }
        output.append(System.lineSeparator());
    }

    private int processInputStream(CommandOptions options, IOEnvironment ioEnvironment) {
        StringBuilder output = new StringBuilder();
        int lines = 0;
        int words = 0;
        int bytes = 0;
        String content;
        try {
            content = ioEnvironment.read();
        } catch (IOException e) {
            ioEnvironment.writeError("wc: error reading input stream" + System.lineSeparator());
            return 1;
        }
        try (Scanner scanner = new Scanner(content)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines++;
                words += line.split("\\s+").length;
                bytes += line.getBytes().length;
            }
        }
        printStatistics(options, output, null, lines, words, bytes);
        try {
            ioEnvironment.writeOutput(output.toString());
        } catch (IOException e) {
            ioEnvironment.writeError("wc: error writing output stream" + System.lineSeparator());
            return 1;
        }
        return 0;
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
     * @param args          List of file names to be analyzed.
     * @param options       Command options specifying which statistics to include.
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status and output.
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            try {
                ioEnvironment.writeOutput(HELP_MESSAGE);
            } catch (IOException e) {
                ioEnvironment.writeError("wc: cannot write help msg" + System.lineSeparator());
                return 1;
            }
            return 0;
        }
        if (args.isEmpty()) {
            return processInputStream(options, ioEnvironment);
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
                ioEnvironment.writeError("wc: cannot read file " + file + System.lineSeparator());
                return 1;
            }
        }
        if (args.size() > 1) {
            printStatistics(options, output, "total", totalLines, totalWords, totalBytes);
        }
        try {
            ioEnvironment.writeOutput(output.toString());
        } catch (IOException e) {
            ioEnvironment.writeError("wc: cannot write to output stream" + System.lineSeparator());
        }

        return 0;
    }
}
