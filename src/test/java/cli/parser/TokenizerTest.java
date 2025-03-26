package cli.parser;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import cli.model.Token;

class TokenizerTest {

        private final Tokenizer tokenizer = new TokenizerImpl();

        @Test
        void testSimpleCommand() {
                assertEquals(
                                List.of(List.of(new Token("ls"), new Token("-l"), new Token("/home"))),
                                tokenizer.tokenize("ls -l /home"));
        }

        @Test
        void testCommandWithPipe() {
                assertEquals(
                                List.of(
                                                List.of(new Token("ls"), new Token("-l")),
                                                List.of(new Token("grep"), new Token("txt"))),
                                tokenizer.tokenize("ls -l | grep txt"));
        }

        @Test
        void testSingleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello World", false))),
                                tokenizer.tokenize("echo 'Hello World'"));
        }

        @Test
        void testDoubleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello World"))),
                                tokenizer.tokenize("echo \"Hello World\""));
        }

        @Test
        void testMixedQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("It's a test"))),
                                tokenizer.tokenize("echo \"It's a test\""));
        }

        @Test
        void testEscapedCharactersInDoubleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello \"World\""))),
                                tokenizer.tokenize("echo \"Hello \\\"World\\\"\""));
        }

        @Test
        void testEscapedCharactersInSingleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello \\\"World\\\"", false))),
                                tokenizer.tokenize("echo 'Hello \\\"World\\\"'"));
        }

        @Test
        void testMultiplePipes() {
                assertEquals(
                                List.of(
                                                List.of(new Token("ls")),
                                                List.of(new Token("grep"), new Token("test")),
                                                List.of(new Token("wc"), new Token("-l"))),
                                tokenizer.tokenize("ls | grep test | wc -l"));
        }

        @Test
        void testEscapedSpace() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello World"))),
                                tokenizer.tokenize("echo Hello\\ World"));
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
                                                List.of(new Token("cat"), new Token("file.txt")),
                                                List.of(new Token("grep"), new Token("pattern")),
                                                List.of(new Token("awk"), new Token("{print $1}"))),
                                tokenizer.tokenize("cat file.txt | grep pattern | awk \"{print $1}\""));
        }

        @Test
        void testVariablesWithDoubleQuotes() {
                String input = "echo \"My name is $USER and I am $AGE years old\"";
                assertEquals(
                                List.of(List.of(new Token("echo"),
                                                new Token("My name is $USER and I am $AGE years old"))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testEscapedQuotesInsideDoubleQuotes() {
                String input = "echo \"Hello \\\"World\\\"\"";
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello \"World\""))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testVariableInsideSingleQuotes() {
                String input = "echo 'My path is $HOME'";
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("My path is $HOME", false))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testMixedQuotesWithEscaping() {
                String input = "echo 'This is a \"quote\" test' | echo \"Another 'test'\"";
                assertEquals(
                                List.of(
                                                List.of(new Token("echo"),
                                                                new Token("This is a \"quote\" test", false)),
                                                List.of(new Token("echo"), new Token("Another 'test'"))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testEscapedSpaceWithQuotes() {
                String input = "echo \"Hello\\ World\"";
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello World"))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testPipeWithQuotes() {
                String input = "echo \"Hello World\" | grep \"World\"";
                assertEquals(
                                List.of(
                                                List.of(new Token("echo"), new Token("Hello World")),
                                                List.of(new Token("grep"), new Token("World"))),
                                tokenizer.tokenize(input));
        }

        @Test
        void testSingleQuotesInsideSingleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("It's a test", false))),
                                tokenizer.tokenize("echo 'It'\\''s a test'"));
        }

        @Test
        void testDoubleQuotesInsideDoubleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("He said, \"Hello!\""))),
                                tokenizer.tokenize("echo \"He said, \\\"Hello!\\\"\""));
        }

        @Test
        void testDoubleQuotesInsideSingleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"),
                                                new Token("This is a \"quote\" inside single quotes", false))),
                                tokenizer.tokenize("echo 'This is a \"quote\" inside single quotes'"));
        }

        @Test
        void testSingleQuotesInsideDoubleQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"),
                                                new Token("This is a 'quote' inside double quotes"))),
                                tokenizer.tokenize("echo \"This is a 'quote' inside double quotes\""));
        }

        @Test
        void testEscapedPipeInsideQuotes() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("This | is inside"))),
                                tokenizer.tokenize("echo \"This \\| is inside\""));
        }

        @Test
        void testMultipleEscapedCharacters() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("Hello \"World\" and \\escaped"))),
                                tokenizer.tokenize("echo \"Hello \\\"World\\\" and \\\\escaped\""));
        }

        @Test
        void testEscapedBackslash() {
                assertEquals(
                                List.of(List.of(new Token("echo"), new Token("backslash \\ test"))),
                                tokenizer.tokenize("echo \"backslash \\\\ test\""));
        }
}