package cli.ioenvironment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            this.errorStream.write(error.getBytes());
        } catch (IOException e) {

        }

    }

    @Override
    public void writeOutput(@NotNull String output) throws IOException {
        this.outputStream.write(output.getBytes());
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
}