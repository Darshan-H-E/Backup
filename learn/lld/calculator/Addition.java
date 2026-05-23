package calculator;

public class Addition implements Operation {
    @Override
    public double apply(double a, double b) {
        return a + b;
    }
}
