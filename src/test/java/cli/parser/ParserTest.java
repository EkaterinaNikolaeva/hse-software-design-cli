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
        String input = "command --option1=value1 --option2=value2 -o";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());
        assertTrue(command.options().getAllOptionValues("option1").contains("value1"));
        assertTrue(command.options().getAllOptionValues("option2").contains("value2"));
        assertTrue(command.options().containsOption("o"));
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
    void testParseCommandWithQuotes() {
        String input = "\"com\"\'mand\' arg1 arg2";

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
        String input = "command | var=123 | cmd2";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(3, commands.size());
        Command command1 = commands.get(0);
        assertEquals("command", command1.name());
        assertEquals(0, command1.args().size());
        assertEquals("=", commands.get(1).name());
        assertEquals(List.of("var", "123"), commands.get(1).args());
        assertEquals("cmd2", commands.get(2).name());

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

    @Test
    void testParserWithVariableSubstitution() {
        env.setVariable("HOME", "/user/home");
        env.setVariable("USER", "john");

        String input = "command $HOME $USER";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());

        assertEquals("/user/home john", String.join(" ", command.args()));
    }

    @Test
    void testParserWithVarQuotesSubstitution() {
        env.setVariable("HOME", "/user/home");
        env.setVariable("USER", "john");

        String input = "command \"$HOME\" \'$USER\'";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());

        assertEquals("/user/home $USER", String.join(" ", command.args()));
    }

    @Test
    void testParserWithNoVariableSubstitution() {
        String input = "command $HOME $USER";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());

        assertEquals(" ", String.join(" ", command.args()));
    }

    @Test
    void testParserWithNoVariableSubstitutionTwice() {
        env.setVariable("HOME", "/user/home");
        env.setVariable("USER", "john");

        String input = "command $HOME$USER";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());

        assertEquals("/user/homejohn", command.args().get(0));
    }

    @Test
    void testParserWithMixedVariablesAndRegularArgs() {
        env.setVariable("HOME", "/user/home");

        String input = "command $HOME arg2";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);
        assertEquals("command", command.name());

        assertEquals("/user/home arg2", String.join(" ", command.args()));
    }

    @Test
    void testParseCommandWithGroupedSingleLetterFlags() {
        String input = "command -abc=value";
        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);

        assertEquals("command", command.name());

        assertTrue(command.options().containsOption("a"));
        assertTrue(command.options().containsOption("b"));
        assertTrue(command.options().containsOption("c"));
        assertEquals(command.options().getAllOptionValues("c"), List.of("value"));
        ;
    }

    @Test
    void testParseCommandWithGroupedLongFlags() {
        String input = "command --abc=value";

        ParsedInput parsedInput = parser.parse(input);
        List<Command> commands = parsedInput.commands();

        assertEquals(1, commands.size());
        Command command = commands.get(0);

        assertEquals("command", command.name());

        assertTrue(command.options().containsOption("abc"));
        assertEquals(command.options().getAllOptionValues("abc"), List.of("value"));
        ;
    }

    @Test
    void testParserOrder() {
        String input1 = "echo -n arg";
        String input2 = "echo arg -n";

        ParsedInput parsedInput1 = parser.parse(input1);
        List<Command> commands1 = parsedInput1.commands();

        ParsedInput parsedInput2 = parser.parse(input2);
        List<Command> commands2 = parsedInput2.commands();

        assertEquals(1, commands1.size());
        Command command = commands1.get(0);
        assertEquals("echo", command.name());
        assertEquals(List.of("arg"), command.args());
        assertTrue(command.options().containsOption("n"));

        assertEquals(1, commands2.size());
        Command command2 = commands2.get(0);
        assertEquals("echo", command2.name());
        assertEquals(List.of("arg"), command2.args());
        assertTrue(command2.options().containsOption("n"));

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
