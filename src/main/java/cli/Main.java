package cli;

import cli.commandexecutor.CommandExecutorImpl;
import cli.environment.EnvironmentImpl;
import cli.exceptions.ExitCommandException;
import cli.exceptions.ParseException;
import cli.exceptions.TerminalErrorException;
import cli.filesystem.FileSystemImpl;
import cli.model.ParsedInput;
import cli.parser.ParserImpl;
import cli.pipelineexecutor.PipelineExecutorImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws Exception {
        FileSystemImpl fileSystem = new FileSystemImpl();
        EnvironmentImpl environment = new EnvironmentImpl();
        ParserImpl parser = new ParserImpl(environment);
        CommandExecutorImpl commandExecutor = new CommandExecutorImpl(environment, fileSystem);
        PipelineExecutorImpl pipelineExecutor = new PipelineExecutorImpl(commandExecutor);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out); // Added semicolon here

        while (true) {
            writer.print("> ");
            writer.flush();
            String input = reader.readLine();

            if (input == null) {
                break;
            }

            try {
                ParsedInput parsedInput = parser.parse(input);
                pipelineExecutor.execute(parsedInput, System.in, System.out, System.out);
            } catch (Exception e) {
                if (e.getCause() instanceof ExitCommandException) {
                    return;
                } else if (e instanceof TerminalErrorException) {
                    continue;
                } else if (e instanceof ParseException){
                    System.err.println(e.getMessage());
                    continue;
                }
                throw e;
            }
        }
    }
}
