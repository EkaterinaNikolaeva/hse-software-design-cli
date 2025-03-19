package cli.parser;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private final Tokenizer tokenizer = new TokenizerImpl();

    @Test
    void testSimpleCommand() {
        assertEquals(
                List.of(List.of("ls", "-l", "/home")),
                tokenizer.tokenize("ls -l /home"));
    }

    @Test
    void testCommandWithPipe() {
        assertEquals(
                List.of(List.of("ls", "-l"), List.of("grep", "txt")),
                tokenizer.tokenize("ls -l | grep txt"));
    }

    @Test
    void testSingleQuotes() {
        assertEquals(
                List.of(List.of("echo", "Hello World")),
                tokenizer.tokenize("echo 'Hello World'"));
    }

    @Test
    void testDoubleQuotes() {
        assertEquals(
                List.of(List.of("echo", "Hello World")),
                tokenizer.tokenize("echo \"Hello World\""));
    }

    @Test
    void testMixedQuotes() {
        assertEquals(
                List.of(List.of("echo", "It's a test")),
                tokenizer.tokenize("echo \"It's a test\""));
    }

    @Test
    void testEscapedCharactersInDoubleQuotes() {
        assertEquals(
                List.of(List.of("echo", "Hello \"World\"")),
                tokenizer.tokenize("echo \"Hello \\\"World\\\"\""));
    }

    @Test
    void testEscapedCharactersInSingleQuotes() {
        assertEquals(
                List.of(List.of("echo", "Hello \\\"World\\\"")),
                tokenizer.tokenize("echo 'Hello \\\"World\\\"'"));
    }

    @Test
    void testMultiplePipes() {
        assertEquals(
                List.of(List.of("ls"), List.of("grep", "test"), List.of("wc", "-l")),
                tokenizer.tokenize("ls | grep test | wc -l"));
    }

    @Test
    void testEscapedSpace() {
        assertEquals(
                List.of(List.of("echo", "Hello World")),
                tokenizer.tokenize("echo Hello\\ World"));
    }

    @Test
    void testSubstitution() {
        assertEquals(
                List.of(List.of("val=1")),
                tokenizer.tokenize("val=1"));
    }

    @Test
    void testEmptyInput() {
        assertEquals(List.of(), tokenizer.tokenize(""));
    }

    @Test
    void testOnlySpaces() {
        assertEquals(List.of(), tokenizer.tokenize("    "));
    }

    @Test
    void testComplexCommand() {
        assertEquals(
                List.of(
                        List.of("cat", "file.txt"),
                        List.of("grep", "pattern"),
                        List.of("awk", "{print $1}")),
                tokenizer.tokenize("cat file.txt | grep pattern | awk \"{print $1}\""));
    }
}
