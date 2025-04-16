package cli.ioenvironment;

import java.io.IOException;

public interface IOEnvironment {
    void writeError(String error);
    void writeOutput(String output) throws IOException;
    String read() throws IOException;
    String readLine() throws IOException;
    boolean hasInput() throws IOException;
}
