package cli.commandexecutor.commands;

import cli.model.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PwdExecutorTest {
    private PwdExecutor pwdExecutor;

    @BeforeEach
    void setUp() {
        pwdExecutor = new PwdExecutor();
    }

    @Test
    void testExecuteWithoutFlags() {
        CommandResult result = pwdExecutor.execute(Collections.emptyList(), Collections.emptyList());
        assertEquals(0, result.exitCode());
        assertEquals(System.getProperty("user.dir"), result.output());
    }

    @Test
    void testExecuteWithHelpFlag() {
        CommandResult result = pwdExecutor.execute(Collections.emptyList(), List.of("help"));
        assertEquals(0, result.exitCode());
        assertEquals("Get current work directory", result.output());
    }

    @Test
    void testExecuteMatchesLaunchDirectory() {
        String launchDir = System.getenv("PWD");
        if (launchDir != null) {
            CommandResult result = pwdExecutor.execute(Collections.emptyList(), Collections.emptyList());
            assertEquals(0, result.exitCode());
            assertEquals(launchDir, result.output(), "user.dir is not equal to PWD");
        }
    }


}