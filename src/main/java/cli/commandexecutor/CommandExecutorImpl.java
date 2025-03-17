package cli.commandexecutor;

import java.io.InputStream;
import java.io.OutputStream;

import cli.environment.Environment;
import cli.exceptions.ExitCommandException;
import cli.model.Command;
import cli.model.CommandResult;

public class CommandExecutorImpl implements CommandExecutor {

    public CommandExecutorImpl(Environment environment) {
        this.environment = environment;
    }

    final Environment environment;

    private CommandResult executeBuiltIn(Command command) {
        return null;
    }

    private CommandResult executeExternal(Command command) {
        return null;
    }

    @Override
    public CommandResult execute(Command command, InputStream input, OutputStream output) throws Exception { //TODO Current for testing only + think about
        switch (command.name()) {
            case "exit" -> throw new ExitCommandException();
            case "ping" -> {
                String s = "pong\n";
                output.write(s.getBytes());
                return new CommandResult(0, s);
            }
            case "echo" -> {
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = input.read()) != -1 && c != '\n') { //TODO Think about '\n' as a EOF symbol or how to read all stream better
                    sb.append((char) c);
                }
                String s = sb.toString();
                s += "\n";
                output.write(s.getBytes());
                return new CommandResult(0, s);
            }
            default -> {
                return new CommandResult(1, "Invalid command");
            }
        }
    }
}
