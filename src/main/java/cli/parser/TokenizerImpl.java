package cli.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * The TokenizerImpl class is responsible for tokenizing the input string into
 * commands and their arguments with proper handling of quotes and escaping.
 * 
 * - Single quotes: treat content literally.
 * - Double quotes: interpret escape sequences.
 * - Backslash: escape special characters inside double quotes.
 * - Pipe: separate commands.
 * 
 * Example:
 * Input: echo 'hello $name' | echo "escaped: \$test"
 * Output: [["echo", "hello $name"], ["echo", "escaped: $test"]]
 */

public class TokenizerImpl implements Tokenizer {

  @Override
  public List<List<String>> tokenize(String input) {
    List<List<String>> commands = new ArrayList<>();
    List<String> currentCommand = new ArrayList<>();
    StringBuilder token = new StringBuilder();

    boolean inSingleQuotes = false;
    boolean inDoubleQuotes = false;
    boolean escaping = false;

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);

      if (escaping) {
        token.append(c);
        escaping = false;
      } else if (c == '\\' && !inSingleQuotes) {
        escaping = true;
      } else if (c == '\'' && !inDoubleQuotes) {
        inSingleQuotes = !inSingleQuotes;
      } else if (c == '"' && !inSingleQuotes) {
        inDoubleQuotes = !inDoubleQuotes;
      } else if (c == '|' && !inSingleQuotes && !inDoubleQuotes) {
        addToken(currentCommand, token);
        commands.add(currentCommand);
        currentCommand = new ArrayList<>();
      } else if (Character.isWhitespace(c) && !inSingleQuotes && !inDoubleQuotes) {
        addToken(currentCommand, token);
      } else {
        token.append(c);
      }
    }

    addToken(currentCommand, token);
    if (!currentCommand.isEmpty()) {
      commands.add(currentCommand);
    }

    return commands;
  }

  private void addToken(List<String> command, StringBuilder token) {
    if (token.length() > 0) {
      command.add(token.toString());
      token.setLength(0);
    }
  }
}