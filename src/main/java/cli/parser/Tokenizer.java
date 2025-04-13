package cli.parser;

import java.util.List;

public interface Tokenizer {
  List<List<String>> tokenize(String input);
}
