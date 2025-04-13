package cli.commandexecutor;

import cli.environment.EnvironmentImpl;
import cli.model.Command;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorImplTest {

    private CommandExecutorImpl executor;

    @BeforeEach
    public void setUp() {
        executor = new CommandExecutorImpl(new EnvironmentImpl());
    }

    @Test
    void testExecuteBuiltIn() {
        Command command = new Command("echo", List.of("aba"), new CommandOptions());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();

        int exitCode = executor.execute(command, InputStream.nullInputStream(), output, error);

        assertEquals(0, exitCode);
        assertEquals("aba" + System.lineSeparator(), output.toString());
        assertTrue(error.toString().isEmpty());
    }

    @Test
    void testExecuteExternal() {
        Command command = new Command("date", List.of("+%Y-%m-%d"), new CommandOptions());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();

        int exitCode = executor.execute(command, InputStream.nullInputStream(), output, error);
        assertEquals(0, exitCode);
        assertEquals(LocalDate.now().toString() + System.lineSeparator(), output.toString());
        assertTrue(error.toString().isEmpty());
    }
}
