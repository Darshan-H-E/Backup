package calculator;

public class Calculator {
    public double calculate(double a, double b, String operator) {
        Operation operation = OperationFactory.getOperation(operator)
                .orElseThrow(() -> new IllegalArgumentException("Invalid operator: " + operator));
        return operation.apply(a, b);
    }
}
