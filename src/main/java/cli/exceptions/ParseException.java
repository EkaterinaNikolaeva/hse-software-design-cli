package cli.exceptions;

public class ParseException {
    public static class UnclosedQuoteException extends RuntimeException {
        public UnclosedQuoteException() {
            super("Syntax error: String literal is not properly closed with a matching quote");
        }
    }

    public static class EmptyPipeException extends RuntimeException {
        public EmptyPipeException() {
            super("Syntax error: Pipe operator '|' cannot be empty (missing command on one or both sides)");
        }
    }
}