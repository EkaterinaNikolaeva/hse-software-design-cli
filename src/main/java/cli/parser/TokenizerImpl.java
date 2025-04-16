package cli.parser;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Set;

import cli.model.Token;
import cli.exceptions.ParseException;
import cli.exceptions.ParseException.EmptyPipeException;
import cli.exceptions.ParseException.UnclosedQuoteException;

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
        final Set<Character> dqEscapes = Set.of('$', '`', '"', '\\', '\n');

        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean isEscaping = false;
        boolean isSubstitute = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (isEscaping && !inDoubleQuotes) {
                token.append(c);
                isEscaping = false;

            } else if (isEscaping && inDoubleQuotes) {
                if (!dqEscapes.contains(c) || c == '$') {
                    token.append('\\');
                }

                isEscaping = false;
                token.append(c);

            } else if (c == '\\') {
                if (inDoubleQuotes || isSpecialChar(peek(input, i + 1)) && !inSingleQuotes) {
                    isEscaping = true;
                } else {
                    token.append(c);
                }
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                isSubstitute = false;

            } else if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                isSubstitute = true;

            } else if (c == '|' && !inSingleQuotes && !inDoubleQuotes) {
                addToken(currentCommand, token, inSingleQuotes);
                if (currentCommand.isEmpty()) {
                    throw new EmptyPipeException();
                }
                commands.add(currentCommand);
                currentCommand = new ArrayList<>();
                isSubstitute = true;

            } else if (Character.isWhitespace(c) && !inSingleQuotes && !inDoubleQuotes) {
                addToken(currentCommand, token, isSubstitute);

            } else {
                if (inSingleQuotes) {
                    token.append(c);
                } else {
                    token.append(c);
                }
            }
        }

        addToken(currentCommand, token, isSubstitute);
        if (currentCommand.isEmpty()) {
            throw new EmptyPipeException();
        }
        commands.add(currentCommand);

        if (inSingleQuotes || inDoubleQuotes) {
            throw new UnclosedQuoteException();
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