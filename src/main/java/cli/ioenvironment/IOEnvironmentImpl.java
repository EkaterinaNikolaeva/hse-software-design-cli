package cli.ioenvironment;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public class IOEnvironmentImpl implements IOEnvironment {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStream errorStream;

    public IOEnvironmentImpl(InputStream inputStream, OutputStream outputStream, OutputStream errorStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }
    @Override
    public void writeError(@NotNull String error) {
        try {
            errorStream.write(error.getBytes());
            errorStream.flush();
        } catch (IOException e) {

        }

    }

    @Override
    public void writeOutput(@NotNull String output) throws IOException {
        outputStream.write(output.getBytes());
        outputStream.flush();
    }

    @Override
    public String read() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\n') {
                break;
            }
            sb.append((char) c);
        }
        if (c == -1 && sb.isEmpty()) {
            return null;
        }
        return sb.toString();
    }
}
