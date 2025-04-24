package cli.parser;

import java.util.ArrayList;
import java.util.List;

import cli.environment.Environment;
import cli.model.Command;
import cli.model.CommandOptions;
import cli.model.ParsedInput;

/**
 * The ParserImpl class is responsible for parsing the input string into
 * commands and their associated arguments and options.
 * 
 * The working principle:
 * 1. The input string is first tokenized using the Tokenizer.
 * 2. Each tokenized command is processed:
 * - The first token is treated as the command name.
 * - Tokens starting with '--' are interpreted as command options, where values
 * may be specified with '='.
 * - Other tokens are treated as arguments.
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
    List<List<String>> tokenized = tokenizer.tokenize(input);
    List<Command> commands = new ArrayList<>();

    for (List<String> tokens : tokenized) {
      if (tokens.isEmpty())
        continue;

      String name = tokens.get(0);
      List<String> args = new ArrayList<>();
      CommandOptions options = new CommandOptions();

      for (int i = 1; i < tokens.size(); i++) {
        String token = tokens.get(i);

        if (token.startsWith("--")) {
          String[] parts = token.substring(2).split("=", 2);

          if (options.getAllOptionValues(parts[0]) == null) {
            options.setOptionValues(parts[0], new ArrayList<>());
          }

          if (parts.length == 2) {
            options.getAllOptionValues(parts[0]).add(parts[1]);
          }
        } else {
          args.add(expandVariables(token));
        }

      }

      commands.add(new Command(name, args, options));
    }

    return new ParsedInput(commands);
  }

  private String expandVariables(String arg) {
    StringBuilder expanded = new StringBuilder();
    StringBuilder varName = new StringBuilder();

    boolean inVar = false;

    for (int i = 0; i < arg.length(); i++) {
      char c = arg.charAt(i);

      if (c == '$' && !inVar) {
        inVar = true;
        varName.setLength(0);
      } else if (inVar && (Character.isLetterOrDigit(c) ||
          c == '_')) {
        varName.append(c);
      } else {
        if (inVar) {
          expanded.append(env.getVariable(varName.toString()));
          inVar = false;
        }
        expanded.append(c);
      }
    }

    if (inVar) {
      expanded.append(env.getVariable(varName.toString()));
    }

    return expanded.toString();
  }
}
