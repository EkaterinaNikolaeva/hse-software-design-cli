package cli.parser;

import java.util.List;
import cli.model.Token;

public interface Tokenizer {
  List<List<Token>> tokenize(String input);
}
