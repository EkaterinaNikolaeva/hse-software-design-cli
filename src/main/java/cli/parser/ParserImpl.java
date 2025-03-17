package cli.parser;

import cli.environment.Environment;
import cli.model.ParsedInput;
import cli.model.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserImpl implements Parser {

    public ParserImpl(Environment environment) {
    }

    @Override
    public ParsedInput parse(String input) { //TODO many things like environment, options, etc
        List<Command> commands = new ArrayList<>();
        String[] parts = input.split("\\|");
        for (String part : parts) {
            String[] commandParts = part.trim().split("\\s+");
            if (commandParts.length > 0) {
                String commandName = commandParts[0];
                List<String> args = new ArrayList<>(Arrays.asList(commandParts).subList(1, commandParts.length));
                commands.add(new Command(commandName, args, null));
            }
        }
        return new ParsedInput(commands);
    }
}
