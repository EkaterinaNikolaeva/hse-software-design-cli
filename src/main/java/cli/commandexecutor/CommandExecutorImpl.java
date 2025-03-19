package cli.commandexecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cli.commandexecutor.commands.CatExecutor;
import cli.commandexecutor.commands.EchoExecutor;
import cli.commandexecutor.commands.ExitExecutor;
import cli.commandexecutor.commands.InternalCommandExecutor;
import cli.commandexecutor.commands.PwdExecutor;
import cli.commandexecutor.commands.WcExecutor;
import cli.environment.Environment;
import cli.model.Command;
import cli.model.CommandResult;

public class CommandExecutorImpl implements CommandExecutor {
    private final Environment environment;
    private final Map<String, InternalCommandExecutor> builtInCommands = new HashMap<>();

    public CommandExecutorImpl(Environment environment) {
        this.environment = environment;
        registerBuiltInCommands();

    }

    private void registerBuiltInCommands() {
        builtInCommands.put("pwd", new PwdExecutor());
        builtInCommands.put("cat", new CatExecutor());
        builtInCommands.put("echo", new EchoExecutor());
        builtInCommands.put("wc", new WcExecutor());
        builtInCommands.put("exit", new ExitExecutor());
    }

    private CommandResult executeBuiltIn(Command command, InputStream input, OutputStream output) throws IOException {
        InternalCommandExecutor executor = builtInCommands.get(command.name());
        if (executor != null) {
            return executor.execute(command.args(), command.options(), input, output);
        }
        return new CommandResult(1, "Unknown built-in command: " + command.name());
    }

    private CommandResult executeExternal(Command command, InputStream input, OutputStream output) {
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
            int exitCode = process.waitFor();
            return new CommandResult(exitCode, "");
        } catch (IOException | InterruptedException e) {
            return new CommandResult(1, "Error executing external command: " + e.getMessage());
        }
    }

    @Override
    public CommandResult execute(Command command, InputStream input, OutputStream output) throws Exception { //TODO Current for testing only + think about
        if (builtInCommands.containsKey(command.name())) {
            return executeBuiltIn(command, input, output);
        } else {
            return executeExternal(command, input, output);
        }

    }
}
