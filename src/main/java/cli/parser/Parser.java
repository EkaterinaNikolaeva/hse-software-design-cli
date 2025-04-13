package cli.parser;

import cli.model.ParsedInput;

public interface Parser {
    ParsedInput parse(String input);
}
