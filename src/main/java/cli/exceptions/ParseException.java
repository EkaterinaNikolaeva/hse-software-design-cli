package cli.exceptions;

public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }

    public static class UnclosedQuoteException extends ParseException {
        public UnclosedQuoteException() {
            super("Syntax error: String literal is not properly closed with a matching quote");
        }
    }

    public static class EmptyPipeException extends ParseException {
        public EmptyPipeException() {
            super("Syntax error: Pipe operator '|' cannot be empty (missing command on one or both sides)");
        }
    }
}