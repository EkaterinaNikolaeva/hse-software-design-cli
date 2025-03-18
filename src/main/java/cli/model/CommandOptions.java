package cli.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CommandOptions {
    private final Map <String, List<String>> options;

    public CommandOptions() {
        this.options = new TreeMap<>();
    }

    public CommandOptions(Map <String, List<String>> options) {
        this.options = options;
    }

    public List <String> getAllOptionValues(String key) {
        return this.options.get(key);
    }

    public boolean containsOption(String key) {
        return this.options.containsKey(key);
    }

    public String getFirstOptionValues(String key) {
        List <String> values = this.options.get(key);
        if (values != null && !values.isEmpty()) {
            return values.getFirst();
        }
        return null;
    }
}
