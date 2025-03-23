package cli.pipelineexecutor;

import cli.model.*;
import cli.commandexecutor.CommandExecutor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.*;

/**
 * The PipelineExecutorImpl class implements the PipelineExecutor interface.
 * It is responsible for executing a sequence of commands, connecting their inputs and outputs,
 * and managing the execution process using a thread pool.
 */
public class PipelineExecutorImpl implements PipelineExecutor {
    private final CommandExecutor commandExecutor;

    public PipelineExecutorImpl(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    /**
     * Executes a pipeline of commands.
     * The method iterates over each command in the pipeline, executing them sequentially
     * while connecting their inputs and outputs using piped streams.
     * It utilizes a thread pool to manage the execution of commands concurrently.
     *
     * @param parsedInput The parsed input representing the pipeline of commands.
     * @param firstInput  The initial input stream for the pipeline.
     * @param lastOutput  The final output stream for the pipeline.
     * @throws Exception If any error occurs during pipeline execution.
     */
    @Override
    public void execute(ParsedInput parsedInput, InputStream firstInput, OutputStream lastOutput) throws Exception {
        List<Command> commands = parsedInput.commands();
        if (commands.isEmpty()) {
            return;
        }
        try (ExecutorService executor = Executors.newFixedThreadPool(commands.size())) {
            InputStream input = firstInput;

            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);

                final boolean isLastCommand = (i == commands.size() - 1);
                PipedOutputStream pipedOutputStream = !isLastCommand ? new PipedOutputStream() : null;
                OutputStream currentOutput = !isLastCommand ? pipedOutputStream : lastOutput;
                InputStream nextInput = !isLastCommand ? new PipedInputStream(pipedOutputStream) : null;
                InputStream currentInput = input;

                Future<CommandResult> future;
                if (isLastCommand) {
                    future = executor.submit(() -> {
                        return commandExecutor.execute(command, currentInput, currentOutput);
                    });
                } else {
                    future = executor.submit(() -> {
                        try (OutputStream out = currentOutput) {
                            return commandExecutor.execute(command, currentInput, out);
                        }
                    });
                }
                future.get();

                if (!isLastCommand) {
                    input = nextInput;
                }
            }
            lastOutput.flush();
            executor.shutdown();
        }
    }
}
