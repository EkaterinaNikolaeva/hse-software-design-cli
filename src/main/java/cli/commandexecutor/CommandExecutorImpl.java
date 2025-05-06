package cli.commandexecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cli.commandexecutor.commands.*;
import cli.environment.Environment;
import cli.exceptions.ExitCommandException;
import cli.filesystem.FileSystem;
import cli.ioenvironment.IOEnvironmentImpl;
import cli.model.Command;
import org.jetbrains.annotations.NotNull;

/**
 * The CommandExecutorImpl class implements the CommandExecutor interface.
 * It is responsible for executing both built-in and external commands.
 */
public class CommandExecutorImpl implements CommandExecutor {
    private final Environment environment;
    private final Map<String, InternalCommandExecutor> builtInCommands = new HashMap<>();
    private final FileSystem fileSystem;

    public CommandExecutorImpl(Environment environment, FileSystem fileSystem) {
        this.environment = environment;
        this.fileSystem = fileSystem;
        registerBuiltInCommands();

    }

    private void registerBuiltInCommands() {
        builtInCommands.put("pwd", new PwdExecutor(fileSystem));
        builtInCommands.put("cat", new CatExecutor(fileSystem));
        builtInCommands.put("echo", new EchoExecutor());
        builtInCommands.put("wc", new WcExecutor(fileSystem));
        builtInCommands.put("exit", new ExitExecutor());
        builtInCommands.put("=", new SetEnvironmentExecutor(environment));
        builtInCommands.put("grep", new GrepExecutor(fileSystem));
    }

    private int executeBuiltIn(@NotNull Command command, InputStream input, OutputStream output, OutputStream error) throws ExitCommandException {
        InternalCommandExecutor executor = builtInCommands.get(command.name());
        if (executor != null) {
            return executor.execute(command.args(), command.options(), new IOEnvironmentImpl(input, output, error));
        }
        return 1;
    }

    private int executeExternal(@NotNull Command command, InputStream input, OutputStream output, OutputStream error) {
        List<String> commandWithArgs = new ArrayList<>();
        commandWithArgs.add(command.name());
        commandWithArgs.addAll(command.args());
        ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            if (input != System.in) {
                try (OutputStream processInput = process.getOutputStream()) {
                    input.transferTo(processInput);
                }
            }
            try (InputStream processOutput = process.getInputStream()) {
                processOutput.transferTo(output);
            }
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            try {
                error.write(("Error executing external command: " + e.getMessage() + System.lineSeparator()).getBytes());
            } catch (IOException ee) {

            }
            return 1;
        }
    }

    /**
     * Executes a command (either built-in or external).
     * The method first checks if the command is a built-in command.
     * If the command is not built-in, it treats it as an external command and runs it via a new process.
     *
     * @param command The command to execute.
     * @param input The input stream for the command.
     * @param output The output stream for the command.
     * @return CommandResult containing the execution status and output.
     * @throws ExitCommandException If exit command was provided.
     */
    @Override
    public int execute(Command command, InputStream input, OutputStream output, OutputStream error) throws ExitCommandException {
        if (builtInCommands.containsKey(command.name())) {
            return executeBuiltIn(command, input, output, error);
        } else {
            return executeExternal(command, input, output, error);
        }

    }
}
