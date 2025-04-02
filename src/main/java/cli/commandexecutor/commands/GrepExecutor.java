package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.model.CommandOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GrepExecutor implements InternalCommandExecutor {
    @Override
    public int execute(List<String> args, CommandOptions options, IOEnvironment ioEnvironment) {
        if (args.size() < 2) {
            ioEnvironment.writeError("Usage: grep [options] <pattern> <file>");
            return 1;
        }

        String patternStr;
        String fileName;

        try {
            boolean wholeWord = options.containsOption("w");
            boolean ignoreCase = options.containsOption("i");
            int afterContext = 0;
            if (options.containsOption("A")) {
                try {
                    afterContext = Integer.parseInt(args.get(0));
                } catch (NumberFormatException e) {
                    ioEnvironment.writeError("Invalid number for -A option");
                    return 1;
                }
                patternStr = args.get(1);
                fileName = args.get(2);
            } else {
                patternStr = args.get(0);
                fileName = args.get(1);
            }

            int flags = ignoreCase ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
            if (wholeWord) {
                patternStr = "\\b" + patternStr + "\\b";
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
            ioEnvironment.writeError("Error reading file: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            ioEnvironment.writeError("Error: " + e.getMessage());
            return 1;
        }
    }
}