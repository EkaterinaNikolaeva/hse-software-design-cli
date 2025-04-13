package cli.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentImplTest {

    private EnvironmentImpl environment;

    @BeforeEach
    void setUp() {
        environment = new EnvironmentImpl();
    }

    @Test
    void testGetVariableNull() {
        String variableName = "var";
        assertNull(environment.getVariable(variableName));
    }

    @Test
    void testSetVariable() {
        String variableName = "var";
        String variableValue = "value";
        environment.setVariable(variableName, variableValue);
        assertEquals(variableValue, environment.getVariable(variableName));
    }

    @Test
    void testSetVariableChangeValue() {
        String variableName = "var";
        String variableValue1 = "value-1";
        String variableValue2 = "value-2";
        environment.setVariable(variableName, variableValue1);
        environment.setVariable(variableName, variableValue2);
        assertEquals(variableValue2, environment.getVariable(variableName));
    }

    @Test
    void testMultipleVariable() {
        String variableName1 = "var-1";
        String variableName2 = "var-2";
        String variableValue1 = "value-1";
        String variableValue2 = "value-2";
        environment.setVariable(variableName1, variableValue1);
        environment.setVariable(variableName2, variableValue2);
        assertEquals(variableValue1, environment.getVariable(variableName1));
        assertEquals(variableValue2, environment.getVariable(variableName2));
    }
}
