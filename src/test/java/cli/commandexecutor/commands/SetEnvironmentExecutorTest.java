package cli.commandexecutor.commands;

import cli.environment.Environment;
import cli.environment.EnvironmentImpl;
import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetEnvironmentExecutorTest {
    private Environment environment;
    private SetEnvironmentExecutor executor;
    private IOEnvironment ioEnvironment;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;

    @BeforeEach
    void setUp() {
        environment = new EnvironmentImpl();
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(new ByteArrayInputStream(new byte[0]), outputStream, errorStream);
        executor = new SetEnvironmentExecutor(environment);
    }

    @Test
    void testExecuteValidArguments() {
        int exitCode = executor.execute(List.of("VAR", "VALUE"), new CommandOptions(), ioEnvironment);
        assertEquals(0, exitCode);
        assertEquals("VALUE", environment.getVariable("VAR"));
    }

    @Test
    void testExecuteTooManyArguments() {
        int exitCode = executor.execute(List.of("VAR", "VALUE", "EXTRA"), new CommandOptions(), ioEnvironment);
        assertEquals(1, exitCode);
        assertEquals("incorrect set variable statement" + System.lineSeparator(), errorStream.toString());
    }
}
