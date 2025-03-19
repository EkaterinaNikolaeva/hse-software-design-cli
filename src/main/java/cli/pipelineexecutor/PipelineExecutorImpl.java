package cli.pipelineexecutor;

import cli.exceptions.ExitCommandException;
import cli.model.*;
import cli.commandexecutor.CommandExecutor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.*;

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
        boolean exitFlag = false;
        try (ExecutorService executor = Executors.newFixedThreadPool(commands.size())) {
            InputStream input = firstInput;
            OutputStream output;

            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);

                OutputStream currentOutput = (i < commands.size() - 1) ? new PipedOutputStream() : lastOutput;
                InputStream nextInput = (i < commands.size() - 1) ? new PipedInputStream((PipedOutputStream) currentOutput) : null;

                InputStream finalInput = input;
                try {
                    Callable<CommandResult> task = () -> commandExecutor.execute(command, finalInput, currentOutput);
                    Future<CommandResult> future = executor.submit(task);
                    future.get();
                } catch (ExecutionException e) {
                    exitFlag = true;
                    break;
                }

                if (nextInput != null) {
                    input = nextInput;
                }
            }
            lastOutput.flush();
            executor.shutdown();
            if (exitFlag) {
                throw new ExitCommandException();
            }
        }


    }
}
