package cli.model;

import java.util.List;
import java.util.Map;

public record Command(String name, List<String> args, Map<String, String> options) {
}
