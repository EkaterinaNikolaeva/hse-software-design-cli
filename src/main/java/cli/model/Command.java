package cli.model;

import java.util.List;

public record Command(String name, List<String> args, List<String> flags) {
}
