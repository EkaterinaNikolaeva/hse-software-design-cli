package cli.commandexecutor.commands;

import cli.model.CommandResult;
import cli.model.CommandOptions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WcExecutor implements InternalCommandExecutor {
    private static final String HELP_MESSAGE = "Print lines, words and bytes in file.";
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
        output.append(file).append("\n");
    }

    private int processWordsNumber(@NotNull StringBuilder output, @NotNull String content) {
        int words = content.split("\\s+").length;
        output.append(words).append(" ");
        return words;
    }

    private int processBytesNumber(@NotNull StringBuilder output, @NotNull String content) {
        int bytes = content.getBytes().length;
        output.append(bytes).append(" ");
        return bytes;
    }

    @Override
    public CommandResult execute(List<String> args, CommandOptions options) {
        if (options != null && options.containsOption(FLAG_HELP)) {
            return new CommandResult(0, HELP_MESSAGE);
        }
        if (args.isEmpty()) {
            return new CommandResult(1, "wc: no file specified");
        }
        StringBuilder output = new StringBuilder();
        int totalLines = 0;
        int totalWords = 0;
        int totalBytes = 0;
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
