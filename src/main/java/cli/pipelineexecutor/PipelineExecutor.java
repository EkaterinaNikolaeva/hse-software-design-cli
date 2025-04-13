package cli.pipelineexecutor;


import java.io.InputStream;
import java.io.OutputStream;

import cli.exceptions.ExitCommandException;
import cli.model.ParsedInput;

/**
 * The PipelineExecutor interface defines the contract for executing command pipelines.
 * Implementations of this interface are responsible for executing a sequence of commands,
 * handling input and output streams between commands, and managing the execution process.
 */
public interface PipelineExecutor {
    /**
     * Executes a pipeline of commands.
     * The method takes a parsed input representing the pipeline and input/output streams.
     * It handles the execution of each command in the pipeline, connecting their inputs and outputs.
     *
     * @param parsedInput The parsed input representing the pipeline of commands.
     * @param input       The initial input stream for the pipeline.
     * @param output      The final output stream for the pipeline.
     * @throws Exception            If any error occurs during pipeline execution.
     * @throws ExitCommandException If exit command was provided.
     */
    void execute(ParsedInput parsedInput, InputStream input, OutputStream output) throws Exception;
}
