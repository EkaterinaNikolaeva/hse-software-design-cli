package cli.parser;

import java.util.ArrayList;
import java.util.List;

import cli.model.Token;

/**
 * The TokenizerImpl class is responsible for tokenizing the input string into
 * commands and their arguments with proper handling of quotes, escaping, and
 * pipes.
 * 
 * - Single quotes: content is treated literally.
 * - Double quotes: interpret escape sequences.
 * - Backslash: escapes special characters only in double quotes.
 * - Pipe: separates commands.
 * 
 * Example:
 * Input: echo 'hello $name' | echo "escaped: \$test"
 * Output: [["echo", "hello $name"], ["echo", "escaped: $test"]]
 */

public class TokenizerImpl implements Tokenizer {

    @Override
    public List<List<Token>> tokenize(String input) {
        List<List<Token>> commands = new ArrayList<>();
        List<Token> currentCommand = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escaping = false;
        boolean substitute = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escaping) {
                token.append(c);
                escaping = false;

            } else if (c == '\\') {
                if (inDoubleQuotes || isSpecialChar(peek(input, i + 1)) && !inSingleQuotes) {
                    escaping = true;
                } else {
                    token.append(c);
                }
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                substitute = false;

            } else if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                substitute = true;

            } else if (c == '|' && !inSingleQuotes && !inDoubleQuotes) {
                addToken(currentCommand, token, inSingleQuotes);
                commands.add(currentCommand);
                currentCommand = new ArrayList<>();
                substitute = true;

            } else if (Character.isWhitespace(c) && !inSingleQuotes && !inDoubleQuotes) {
                addToken(currentCommand, token, substitute);

            } else {
                if (inSingleQuotes) {
                    token.append(c);
                } else {
                    token.append(c);
                }
            }
        }

        addToken(currentCommand, token, substitute);
        if (!currentCommand.isEmpty()) {
            commands.add(currentCommand);
        }

        return commands;
    }

    private void addToken(List<Token> command, StringBuilder token, boolean substitute) {
        if (token.length() > 0) {
            command.add(new Token(token.toString(), substitute));
            token.setLength(0);
        }
    }

    private char peek(String input, int index) {
        return (index < input.length()) ? input.charAt(index) : '\0';
    }

    private boolean isSpecialChar(char c) {
        return c == ' ' || c == '|' || c == '$' || c == '"' || c == '\'' || c == '\\';
    }
}