package cli.exceptions;

public class ExitCommandException extends RuntimeException {
    public ExitCommandException() {
        super("Exit called");
    }
}
