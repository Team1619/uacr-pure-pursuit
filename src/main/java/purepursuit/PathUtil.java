package purepursuit;

public class PathUtil {

    private PathUtil() {

    }

    public static double angleWrap(double angle) {
        angle += 180;
        angle %= 360;
        angle -= 360;
        angle %= 360;
        return angle + 180;
    }

    public static boolean toleranceEquals(double value1, double value2, double tolerance) {
        return Math.abs(value1 - value2) <= tolerance;
    }

    public static double limit(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double interpolate(double input, double minInput, double maxInput, double minOutput, double maxOutput) {
        return minOutput + ((maxOutput - minOutput) * ((limit(input, minInput, maxInput) - minInput) / (maxInput - minInput)));
    }
}
