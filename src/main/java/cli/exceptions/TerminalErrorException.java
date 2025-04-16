package cli.exceptions;

public class TerminalErrorException extends RuntimeException {
    public TerminalErrorException() {
        super("Terminal error");
    }
}
