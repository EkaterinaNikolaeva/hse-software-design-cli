package cli.pipelineexecutor;


import java.io.InputStream;
import java.io.OutputStream;

import cli.model.ParsedInput;

public interface PipelineExecutor {
    void execute(ParsedInput parsedInput, InputStream input, OutputStream output) throws Exception;
}
