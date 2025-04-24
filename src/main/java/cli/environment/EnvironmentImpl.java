package cli.environment;

import java.util.HashMap;
import java.util.Map;

/**
 * The EnvironmentImpl class implements the Environment interface.
 * It provides a basic implementation for managing environment variables using a map.
 */
public class EnvironmentImpl implements Environment {
    private final Map<String, String> variables;

    public EnvironmentImpl() {
        this.variables = new HashMap<>();
    }

    /**
     * Retrieves the value of a specified environment variable.
     * If the variable does not exist, it returns null.
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable, or null if it does not exist.
     */
    @Override
    public String getVariable(String name) {
        return variables.get(name);
    }

    /**
     * Sets the value of a specified environment variable.
     * If the variable already exists, its value is updated; otherwise, a new entry is created.
     *
     * @param name  The name of the variable to set.
     * @param value The new value for the variable.
     */
    @Override
    public void setVariable(String name, String value) {
        variables.put(name, value);
    }
}
