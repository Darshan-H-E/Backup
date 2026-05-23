import calculator.Calculator;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        double num1 = 100;
        double num2 = 25;

        System.out.println("Calculator LLD Demo");
        System.out.println("====================");
        System.out.println("Number 1: " + num1);
        System.out.println("Number 2: " + num2);
        System.out.println("--------------------");

        // Addition
        double sum = calculator.calculate(num1, num2, "+");
        System.out.println("Addition (+): " + sum);

        // Subtraction
        double difference = calculator.calculate(num1, num2, "-");
        System.out.println("Subtraction (-): " + difference);

        // Multiplication
        double product = calculator.calculate(num1, num2, "*");
        System.out.println("Multiplication (*): " + product);

        // Division
        double quotient = calculator.calculate(num1, num2, "/");
        System.out.println("Division (/): " + quotient);

        // Example of invalid operator
        try {
            calculator.calculate(num1, num2, "%");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Operator Test: " + e.getMessage());
        }

        // Example of division by zero
        try {
            calculator.calculate(num1, 0, "/");
        } catch (ArithmeticException e) {
            System.out.println("Division by Zero Test: " + e.getMessage());
        }
    }
}
