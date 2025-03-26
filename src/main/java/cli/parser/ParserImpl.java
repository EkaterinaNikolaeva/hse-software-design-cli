package cli.parser;

import java.util.ArrayList;
import java.util.List;

import cli.environment.Environment;
import cli.model.Command;
import cli.model.CommandOptions;
import cli.model.ParsedInput;
import cli.model.Token;

/**
 * The ParserImpl class is responsible for parsing the input string into
 * commands and their associated arguments and options.
 * 
 * The working principle:
 * 1. The input string is first tokenized using the Tokenizer.
 * 2. Each tokenized command is processed:
 * - The first token is treated as the command name.
 * - Tokens starting with '--' are interpreted as command options, where values
 * may be specified with '=' or next token.
 * - Tokens starting with `-` (short flags) are treated as command options,
 * where
 * each character (after the first `-`) represents an individual option.
 * - For example, `-abc` would be parsed as three separate flags `-a`, `-b`, and
 * `-c`.
 * - If a flag is followed by a value (e.g., `-o value`), it is assigned to the
 * option. * - Other tokens are treated as arguments.
 * 3. Command options are stored in a CommandOptions object, where each option
 * can have multiple values.
 * 4. Variables within the arguments (denoted by '$') are expanded based on the
 * environment.
 * 5. A list of Command objects, each containing the command name, arguments,
 * and options, is returned.
 * 
 * Example:
 * Input: "command1 arg1 --option=value | command2 arg2 --flag"
 * Output: ParsedInput containing two commands:
 * Command 1: name="command1", args=["arg1"], options={"option": ["value"]}
 * Command 2: name="command2", args=["arg2"], options={"flag": []}
 */

public class ParserImpl implements Parser {

    private final Tokenizer tokenizer;
    private final Environment env;

    public ParserImpl(Environment env) {
        this.tokenizer = new TokenizerImpl();
        this.env = env;
    }

    @Override
    public ParsedInput parse(String input) {
        List<List<Token>> tokenized = tokenizer.tokenize(input);
        List<Command> commands = new ArrayList<>();

        for (List<Token> tokens : tokenized) {
            if (tokens.isEmpty())
                continue;

            String name = expandVariables(tokens.get(0));
            List<String> args = new ArrayList<>();
            CommandOptions options = new CommandOptions();

            String lastFlag = null;

            for (int i = 1; i < tokens.size(); i++) {
                String token = expandVariables(tokens.get(i));

                if (token.startsWith("--")) {
                    String[] parts = token.substring(2).split("=", 2);
                    if (options.getAllOptionValues(parts[0]) == null) {
                        options.setOptionValues(parts[0], new ArrayList<>());
                    }
                    if (parts.length == 2) {
                        options.getAllOptionValues(parts[0]).add(parts[1]);
                    } else {
                        lastFlag = parts[0];
                    }
                } else if (token.contains("=") && !token.startsWith("--")) {
                    String[] parts = token.split("=", 2);
                    commands.add(new Command("=", List.of(parts[0], parts[1]), new CommandOptions()));
                } else if (token.startsWith("-") && token.length() > 1) {
                    for (int j = 1; j < token.length(); j++) {
                        String flag = String.valueOf(token.charAt(j));
                        if (options.getAllOptionValues(flag) == null) {
                            options.setOptionValues(flag, new ArrayList<>());
                        }
                        lastFlag = flag;
                    }
                } else if (lastFlag != null) {
                    options.setOptionValues(lastFlag, List.of(token));
                    lastFlag = null;
                } else {
                    args.add(token);
                }

            }

            commands.add(new Command(name, args, options));
        }

        return new ParsedInput(commands);
    }

    String expandVariables(Token token) {
        String arg = token.value();
        StringBuilder expanded = new StringBuilder();
        StringBuilder varName = new StringBuilder();

        boolean inVar = false;

        if (token.substitute()) {
            for (int i = 0; i < arg.length(); i++) {
                char c = arg.charAt(i);

                if (c == '$' && !inVar) {
                    inVar = true;
                    varName.setLength(0);
                } else if (inVar && (Character.isLetterOrDigit(c) || c == '_')) {
                    varName.append(c);
                } else {
                    if (inVar) {
                        expanded.append(
                                env.getVariable(varName.toString()) != null ? env.getVariable(varName.toString()) : "");
                        inVar = false;
                    }
                    if (c == '$') {
                        inVar = true;
                        varName.setLength(0);
                    } else {
                        expanded.append(c);
                    }
                }
            }
            if (inVar) {
                expanded.append(env.getVariable(varName.toString()) != null ? env.getVariable(varName.toString()) : "");
            }
        } else {
            expanded.append(arg);
        }

        return expanded.toString();
    }

}
