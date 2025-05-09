package cli.pipelineexecutor;

import cli.commandexecutor.CommandExecutor;
import cli.exceptions.ExitCommandException;
import cli.model.Command;
import cli.model.CommandOptions;
import cli.model.ParsedInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineExecutorTest {
    private PipelineExecutor pipelineExecutor;
    private MockCommandExecutor commandExecutor;
    private ByteArrayOutputStream errorOutput;

    @BeforeEach
    void setUp() {
        commandExecutor = new MockCommandExecutor();
        pipelineExecutor = new PipelineExecutorImpl(commandExecutor);
        errorOutput = new ByteArrayOutputStream();
    }

    @Test
    void testCommandWriteArg() throws Exception {
        ParsedInput input = new ParsedInput(List.of(
                new Command("write_arg", List.of("hello", " world"), new CommandOptions())
        ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        pipelineExecutor.execute(input, new ByteArrayInputStream(new byte[0]), output, new PrintStream(errorOutput));

        assertEquals("hello world", output.toString().trim());
        assertTrue(errorOutput.toString().isEmpty());
    }

    @Test
    void testCommandWriteInput() throws Exception {
        ParsedInput input = new ParsedInput(List.of(
                new Command("write_arg", List.of("hello", " world"), new CommandOptions()),
                new Command("write_input", List.of(), new CommandOptions())
        ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        pipelineExecutor.execute(input, new ByteArrayInputStream(new byte[0]), output, new PrintStream(errorOutput));

        assertEquals("hello world", output.toString().trim());
        assertTrue(errorOutput.toString().isEmpty());
    }

    @Test
    void testTwoCommandWriteInputAndArg() throws Exception {
        ParsedInput input = new ParsedInput(List.of(
                new Command("write_arg", List.of("hello"), new CommandOptions()),
                new Command("write_input_and_arg", List.of(" world"), new CommandOptions())
        ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        pipelineExecutor.execute(input, new ByteArrayInputStream(new byte[0]), output, new PrintStream(errorOutput));
        assertEquals("hello world", output.toString().trim());
        assertTrue(errorOutput.toString().isEmpty());
    }

    @Test
    void testTwoCommandsWriteArgsSeparately() throws Exception {
        ParsedInput input1 = new ParsedInput(List.of(
                new Command("write_arg", List.of("hello"), new CommandOptions())
        ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        pipelineExecutor.execute(input1, new ByteArrayInputStream(new byte[0]), output, new PrintStream(errorOutput));

        assertEquals("hello", output.toString().trim());
        assertTrue(errorOutput.toString().isEmpty());

        ParsedInput input2 = new ParsedInput(List.of(
                new Command("write_arg", List.of(" world"), new CommandOptions())
        ));

        pipelineExecutor.execute(input2, new ByteArrayInputStream(new byte[0]), output, new PrintStream(errorOutput));

        assertEquals("hello world", output.toString().trim());
        assertTrue(errorOutput.toString().isEmpty());
    }
}

class MockCommandExecutor implements CommandExecutor {
    private boolean exitCalled = false;

    @Override
    public int execute(Command command, InputStream input, OutputStream output, OutputStream error) throws ExitCommandException {
        try {
            switch (command.name()) {
                case "exit" -> {
                    exitCalled = true;
                    throw new ExitCommandException();
                }
                case "write_input" -> {
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = input.read()) != -1) {
                        sb.append((char) c);
                    }
                    String s = sb.toString();
                    output.write(s.getBytes());
                    return 0;
                }
                case "write_arg" -> {
                    StringBuilder sb = new StringBuilder();
                    for (String args : command.args()) {
                        sb.append(args);
                    }
                    String s = sb.toString();
                    output.write(s.getBytes());
                    return 0;
                }
                case "write_input_and_arg" -> {
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = input.read()) != -1) {
                        sb.append((char) c);
                    }
                    for (String args : command.args()) {
                        sb.append(args);
                    }
                    String s = sb.toString();
                    output.write(s.getBytes());
                    return 0;
                }
            }
        } catch (IOException e) {

        }
        return 1;
    }

    public boolean isExitCalled() {
        return exitCalled;
    }
}
