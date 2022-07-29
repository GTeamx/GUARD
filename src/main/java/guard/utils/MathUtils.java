package guard.utils;

import java.util.Collection;

public class MathUtils {

    public static final double EXPANDER = Math.pow(2, 24);

    public static double getAverage(final Collection<? extends Number> data) {
        return data.stream().mapToDouble(Number::doubleValue).average().orElse(0D);
    }

    public static double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);
        return Math.sqrt(variance);
    }

    public static double getVariance(final Collection<? extends Number> data) {
        int count = 0;
        double sum = 0.0;
        double variance = 0.0;
        double average;
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }
        average = sum / count;
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }
        return variance;
    }

    public static double getGcd(final double current, final double previous) {
        return (previous <= 16384) ? current : getGcd(previous, current % previous);
    }

}
