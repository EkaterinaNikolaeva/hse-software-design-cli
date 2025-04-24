package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EchoExecutorTest {
    private EchoExecutor echoExecutor;
    ByteArrayOutputStream outputStream;
    IOEnvironment ioEnvironment;


    @BeforeEach
    void setUp() {
        echoExecutor = new EchoExecutor();
        outputStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, System.err);
    }

    @Test
    void testEcho() {
        int result = echoExecutor.execute(List.of("Hello", "world"), null, ioEnvironment);
        assertEquals(0, result);
        assertEquals("Hello world\n", outputStream.toString());
    }

    @Test
    void testEchoNoNewline() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("n", null);
        int result = echoExecutor.execute(List.of("Hello", "world"), new CommandOptions(options), ioEnvironment);
        assertEquals(0, result);
        assertEquals("Hello world", outputStream.toString());
    }

    @Test
    void testEchoEmpty() {
        int result = echoExecutor.execute(List.of(), null, ioEnvironment);
        assertEquals(0, result);
        assertEquals("\n", outputStream.toString());
    }

    @Test
    void testEchoHelpMessage() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("help", null);
        int result = echoExecutor.execute(List.of("Hello", "world"), new CommandOptions(options), ioEnvironment);
        assertEquals(0, result);
        assertEquals("Display a line of text.\n", outputStream.toString());
    }

}
