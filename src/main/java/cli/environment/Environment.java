package cli.environment;

public interface Environment {
    String getVariable(String name);
    void setVariable(String name, String value);
}
