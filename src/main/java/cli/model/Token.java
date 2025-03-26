package cli.model;

public record Token(
        String value,
        boolean substitute) {
    public Token(String val) {
        this(val, true);
    }
}
