package engine.core.lmsr;

public final class LmsrMath {

    private LmsrMath() {
    }

    public static double cost(double qYes, double qNo, double b) {
        double a = qYes / b;
        double c = qNo / b;
        double m = Math.max(a, c);
        double diff = Math.abs(a - c);
        return b * (m + Math.log1p(Math.exp(-diff)));
    }

    public static double price(double qSelf, double qOther, double b) {
        return 1.0 / (1.0 + Math.exp((qOther - qSelf) / b));
    }

    public static double seed(double b) {
        return cost(0, 0, b);
    }
}
