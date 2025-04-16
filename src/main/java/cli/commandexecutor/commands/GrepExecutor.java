package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The GrepExecutor class implements the InternalCommandExecutor interface
 * and provides functionality similar to the Unix "grep" command.
 * It searches for lines matching a specified pattern in a file.
 */
public class GrepExecutor implements InternalCommandExecutor {

    /**
     * Executes the "grep" command to search for patterns in a file.
     * Supports options for whole-word matching, case-insensitive search,
     * and displaying context lines after matches.
     *
     * @param args          Command arguments containing pattern, filename and optional number
     * @param options       Command flags (options like -w, -i, -A)
     * @param ioEnvironment input, output and error streams
     * @return CommandResult containing the execution status (0 for success, 1 for error)
     */
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {

        String patternStr;
        String fileName;

        try {
            boolean wholeWord = options.containsOption("w");
            boolean ignoreCase = options.containsOption("i");
            int afterContext = 0;
            if (options.containsOption("A")) {
                if (args.size() < 3) {
                    ioEnvironment.writeError("grep: invalid number of arguments" + System.lineSeparator() + "grep [options] -A <number> <pattern> <file>" + System.lineSeparator());
                    return 1;
                }
                try {
                    afterContext = Integer.parseInt(args.get(0));
                } catch (NumberFormatException e) {
                    ioEnvironment.writeError("grep: Invalid number for -A option" + System.lineSeparator());
                    return 1;
                }
                patternStr = args.get(1);
                fileName = args.get(2);
            } else {
                if (args.size() < 2) {
                    ioEnvironment.writeError("grep: invalid number of arguments" + System.lineSeparator() + "grep [options] <pattern> <file>" + System.lineSeparator());
                    return 1;
                }
                patternStr = args.get(0);
                fileName = args.get(1);
            }

            int flags = ignoreCase ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
            if (wholeWord) {
                String escapedPattern = Pattern.quote(patternStr);
                patternStr = "(?<=\\W|^)" + escapedPattern + "(?=\\W|$)";
            }
            Pattern pattern = Pattern.compile(patternStr, flags);

            List<String> lines = Files.readAllLines(Path.of(fileName));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ioEnvironment.writeOutput(line + System.lineSeparator());

                    if (afterContext > 0) {
                        int end = Math.min(i + afterContext + 1, lines.size());
                        for (int j = i + 1; j < end; j++) {
                            ioEnvironment.writeOutput(lines.get(j) + System.lineSeparator());
                        }
                        i = end - 1;
                    }
                }
            }

            return 0;
        } catch (IOException e) {
            ioEnvironment.writeError("grep: error reading file " + e.getMessage() + System.lineSeparator());
            return 1;
        } catch (Exception e) {
            ioEnvironment.writeError("grep: error " + e.getMessage() + System.lineSeparator());
            return 1;
        }
    }
}