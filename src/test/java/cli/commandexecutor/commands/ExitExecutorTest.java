package cli.commandexecutor.commands;

import cli.exceptions.ExitCommandException;
import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ExitExecutorTest {
    @Test
    void testExecute() {
        ExitExecutor executor = new ExitExecutor();
        IOEnvironment ioEnvironment = new IOEnvironmentImpl(System.in, System.out, System.err);
        assertThrows(
                ExitCommandException.class,
                () -> executor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment),
                "Expected execute() to throw ExitCommandException"
        );
    }
}
