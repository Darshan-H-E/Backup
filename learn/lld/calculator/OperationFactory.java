package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OperationFactory {
    private static final Map<String, Operation> operationMap = new HashMap<>();

    static {
        operationMap.put("+", new Addition());
        operationMap.put("-", new Subtraction());
        operationMap.put("*", new Multiplication());
        operationMap.put("/", new Division());
    }

    public static Optional<Operation> getOperation(String operator) {
        return Optional.ofNullable(operationMap.get(operator));
    }
}
