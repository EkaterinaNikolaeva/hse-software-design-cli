package cli.environment;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentImpl implements Environment {
    private final Map<String, String> variables;

    public EnvironmentImpl() {
        this.variables = new HashMap<>();
    }

    @Override
    public String getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void setVariable(String name, String value) {
        variables.put(name, value);
    }
}
