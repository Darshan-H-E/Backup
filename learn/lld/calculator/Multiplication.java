package calculator;

public class Multiplication implements Operation {
    @Override
    public double apply(double a, double b) {
        return a * b;
    }
}
