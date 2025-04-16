package cli.environment;

/**
 * The Environment interface defines the contract for managing environment variables.
 * Implementations of this interface are responsible for storing and retrieving variable values.
 */
public interface Environment {
    /**
     * Retrieves the value of a specified environment variable.
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable, or null if it does not exist.
     */
    String getVariable(String name);

    /**
     * Sets the value of a specified environment variable.
     *
     * @param name  The name of the variable to set.
     * @param value The new value for the variable.
     */
    void setVariable(String name, String value);
}
