package cli.parser;

import cli.environment.Environment;
import cli.model.Command;
import cli.model.ParsedInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Environment env;
    private ParserImpl parser;

    @BeforeEach
    void setUp() {
        env = new MockEnvironment();
        parser = new ParserImpl(env);
    }

    @Test
    void testParseCommandWithOptions() {
        String input = "command --option1=value1 --option2=value2";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());
        assertTrue(command.options().getAllOptionValues("option1").contains("value1"));
        assertTrue(command.options().getAllOptionValues("option2").contains("value2"));
    }

    @Test
    void testParseCommandWithoutOptions() {
        String input = "command arg1 arg2";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());
        assertEquals(2, command.args().size());
        assertTrue(command.args().contains("arg1"));
        assertTrue(command.args().contains("arg2"));
    }

    @Test
    void testParseCommandWithVariableAssignment() {
        String input = "command | var=123";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(2, commands.size());
        Command command1 = commands.get(0);
        assertEquals("command", command1.name());
        assertEquals(0, command1.args().size());
        assertEquals("var=123", commands.get(1).name());
    }

    @Test
    void testParseCommandWithPipe() {
        String input = "command1 arg1 | command2 arg2";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(2, commands.size());

        Command command1 = commands.get(0);
        assertEquals("command1", command1.name());
        assertTrue(command1.args().contains("arg1"));

        Command command2 = commands.get(1);
        assertEquals("command2", command2.name());
        assertTrue(command2.args().contains("arg2"));
    }

    @Test
    void testParseCommandWithEmptyInput() {
        String input = "";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(0, commands.size());
    }
}

class MockEnvironment implements Environment {
    private final java.util.Map<String, String> variables = new java.util.HashMap<>();

    @Override
    public String getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void setVariable(String name, String value) {
        variables.put(name, value);
    }
}
