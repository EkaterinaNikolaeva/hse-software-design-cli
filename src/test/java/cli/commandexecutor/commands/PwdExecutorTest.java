package cli.commandexecutor.commands;

import cli.ioenvironment.IOEnvironment;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.CommandOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PwdExecutorTest {
    private PwdExecutor pwdExecutor;
    ByteArrayOutputStream outputStream;
    ByteArrayOutputStream errorStream;
    IOEnvironment ioEnvironment;

    @BeforeEach
    void setUp() {
        pwdExecutor = new PwdExecutor();
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        ioEnvironment = new IOEnvironmentImpl(System.in, outputStream, errorStream);
    }

    @Test
    void testExecuteWithoutFlags() {
        int result = pwdExecutor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment);
        assertEquals(0, result);
        assertEquals(System.getProperty("user.dir") + System.lineSeparator(), outputStream.toString());
    }

    @Test
    void testExecuteWithHelpFlag() {
        Map<String, List<String>> helpOptions = new HashMap<>();
        helpOptions.put("help", null);
        int result = pwdExecutor.execute(Collections.emptyList(), new CommandOptions(helpOptions), ioEnvironment);
        assertEquals(0, result);
        assertEquals("Get current work directory\n", outputStream.toString());
    }

    @Test
    void testExecuteMatchesLaunchDirectory() {
        String launchDir = System.getenv("PWD");
        if (launchDir != null) {
            int result = pwdExecutor.execute(Collections.emptyList(), new CommandOptions(), ioEnvironment);
            assertEquals(0, result);
            assertEquals(launchDir + System.lineSeparator(), outputStream.toString(), "user.dir is not equal to PWD");
        }
    }
}
