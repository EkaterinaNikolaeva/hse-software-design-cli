package cli.commandexecutor.commands;

import cli.model.CommandOptions;
import cli.model.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EchoExecutorTest {
    private EchoExecutor echoExecutor;

    @BeforeEach
    void setUp() {
        echoExecutor = new EchoExecutor();
    }

    @Test
    void testEcho() {
        CommandResult result = echoExecutor.execute(List.of("Hello", "world"), null, System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("Hello world\n", result.output());
    }

    @Test
    void testEchoNoNewline() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("n", null);
        CommandResult result = echoExecutor.execute(List.of("Hello", "world"), new CommandOptions(options), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("Hello world", result.output());
    }

    @Test
    void testEchoEmpty() {
        CommandResult result = echoExecutor.execute(List.of(), null, System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("\n", result.output());
    }

    @Test
    void testEchoHelpMessage() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("help", null);
        CommandResult result = echoExecutor.execute(List.of("Hello", "world"), new CommandOptions(options), System.in, System.out);
        assertEquals(0, result.exitCode());
        assertEquals("Display a line of text.\n", result.output());
    }

}