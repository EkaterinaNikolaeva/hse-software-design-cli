package cli.commandexecutor.commands;

import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The GrepExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "grep" command.
 * It searches for lines matching a specified pattern in a file or input stream.
 */
public class GrepExecutor implements InternalCommandExecutor {

    // ANSI color codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    private final FileSystem fileSystem;

    public GrepExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Highlights all matches of the pattern in the line with red color
     *
     * @param line    the input line
     * @param pattern the compiled pattern to match
     * @return the line with highlighted matches
     */
    private String highlightMatches(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        StringBuilder coloredLine = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(coloredLine, ANSI_RED + matcher.group() + ANSI_RESET);
        }
        matcher.appendTail(coloredLine);

        return coloredLine.toString();
    }

    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        String patternStr;
        String fileName = null;
        boolean useFile = false;
        boolean colorOutput = options.containsOption("c") || options.containsOption("color");

        try {
            boolean wholeWord = options.containsOption("w");
            boolean ignoreCase = options.containsOption("i");
            int afterContext = 0;

            if (options.containsOption("A")) {
                if (args.size() < 2) {
                    ioEnvironment.writeError("grep: invalid number of arguments or empty input stream" + System.lineSeparator() +
                            "grep [options] -A <number> <pattern> [file]" + System.lineSeparator());
                    return 1;
                }
                try {
                    afterContext = Integer.parseInt(args.get(0));
                    patternStr = args.get(1);
                    if (args.size() > 2) {
                        fileName = args.get(2);
                        useFile = true;
                    }
                } catch (NumberFormatException e) {
                    ioEnvironment.writeError("grep: Invalid number for -A option" + System.lineSeparator());
                    return 1;
                }
            } else {
                if (args.size() < 1) {
                    ioEnvironment.writeError("grep: invalid number of arguments or empty input stream" + System.lineSeparator() +
                            "grep [options] <pattern> [file]" + System.lineSeparator());
                    return 1;
                }
                patternStr = args.get(0);
                if (args.size() > 1) {
                    fileName = args.get(1);
                    useFile = true;
                }
            }

            if (useFile && !Files.exists(fileSystem.resolvePath(Path.of(fileName)))) {
                ioEnvironment.writeError("grep: " + fileName + ": No such file or directory" + System.lineSeparator());
                return 1;
            }

            int flags = ignoreCase ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
            flags |= Pattern.UNICODE_CHARACTER_CLASS;
            if (wholeWord) {
                String escapedPattern = Pattern.quote(patternStr);
                patternStr = "(?<=\\b|^)" + escapedPattern + "(?=\\b|$)";
            }
            Pattern pattern = Pattern.compile(patternStr, flags);

            List<String> lines;
            if (useFile) {
                lines = Files.readAllLines(fileSystem.resolvePath(Path.of(fileName)));
            } else {
                lines = new ArrayList<>();
                String line;
                while ((line = ioEnvironment.readLine()) != null) {
                    lines.add(line);
                }
            }

            for (int i = 0; i < lines.size(); ) {
                String line = lines.get(i++);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String outputLine = colorOutput ? highlightMatches(line, pattern) : line;
                    ioEnvironment.writeOutput(outputLine + System.lineSeparator());

                    if (afterContext > 0) {
                        int end = Math.min(i + afterContext, lines.size());
                        while (i < end) {
                            line = lines.get(i++);
                            matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                outputLine = colorOutput ? highlightMatches(line, pattern) : line;
                                ioEnvironment.writeOutput(outputLine + System.lineSeparator());
                                end = Math.min(i + afterContext, lines.size());
                            } else {
                                ioEnvironment.writeOutput(line + System.lineSeparator());
                            }
                        }
                    }
                }
            }

            return 0;
        } catch (IOException e) {
            ioEnvironment.writeError("grep: " + e.getMessage() + System.lineSeparator());
            return 1;
        }
    }
}