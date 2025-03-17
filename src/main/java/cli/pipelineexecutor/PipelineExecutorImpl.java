package cli.pipelineexecutor;

import cli.model.*;
import cli.commandexecutor.CommandExecutor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PipelineExecutorImpl implements PipelineExecutor {
    private final CommandExecutor commandExecutor;

    public PipelineExecutorImpl(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public void execute(ParsedInput parsedInput, InputStream firstInput, OutputStream lastOutput) throws Exception {
        List<Command> commands = parsedInput.commands();
        if (commands.isEmpty()) {
            return;
        }
        try (ExecutorService executor = Executors.newFixedThreadPool(commands.size())) {
            InputStream input = firstInput;
            OutputStream output;

            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);

                OutputStream currentOutput = (i < commands.size() - 1) ? new PipedOutputStream() : lastOutput;
                InputStream nextInput = (i < commands.size() - 1) ? new PipedInputStream((PipedOutputStream) currentOutput) : null;

                InputStream finalInput = input;
                Callable<CommandResult> task = () -> commandExecutor.execute(command, finalInput, currentOutput);

                Future<CommandResult> future = executor.submit(task);
                CommandResult result = future.get();

                if (nextInput != null) {
                    input = nextInput;
                }
            }

            executor.shutdown();
        }


    }
}
