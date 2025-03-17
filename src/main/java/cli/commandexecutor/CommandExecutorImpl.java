package cli.commandexecutor;

import java.io.IOException;
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

    Environment environment;

    private CommandResult executeBuiltIn(Command command) {
        return null;
    }

    private CommandResult executeExternal(Command command) {
        return null;
    }

    @Override
    public CommandResult execute(Command command, InputStream input, OutputStream output) throws Exception { //TODO Current for testing only + think about
        System.out.println("executer :" + command);
        if (command.name().equals("exit")) {
            throw new ExitCommandException();
        } else if (command.name().equals("privet")) {
            String s = "poka\n";
            output.write(s.getBytes());
            return new CommandResult(0, s);
        } else if (command.name().equals("next")) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = input.read()) != -1 && c!='\n') { //TODO think about '\n'
                sb.append((char) c);
            }
            String s = sb.toString();
            s += " one more command: " + command.name() + "\n";
            output.write(s.getBytes());
            return new CommandResult(0, s);
        } else {
            return new CommandResult(1, "Invalid command");
        }
    }
}
