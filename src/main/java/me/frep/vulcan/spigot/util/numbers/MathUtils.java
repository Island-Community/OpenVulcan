package me.frep.vulcan.spigot.util.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.frep.vulcan.spigot.player.session.PlayerData;

public final class MathUtils {

    public static final double ROTATION_GCD_SCALE = 16777216.0;

    private MathUtils() {
    }

    public static double hypot(double x, double z) {
        return Math.sqrt(x * x + z * z);
    }

    public static double length3(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static String formatNumber(double value) {
        return new DecimalFormat("#.##").format(value);
    }

    public static float angleDifference(float first, float second) {
        return Math.abs(FastMath.wrapDegrees(first - second));
    }

    public static double angleDifference(double first, double second) {
        return Math.abs(FastMath.wrapDegrees(first - second));
    }

    public static double absoluteWrappedAngleDifference(double first, double second) {
        return Math.abs(first % 360.0 - second % 360.0);
    }

    public static double sumSquaredDeviations(Collection<? extends Number> values) {
        double mean = average(values);
        double sum = 0;
        for (Number value : values) {
            double difference = value.doubleValue() - mean;
            sum += difference * difference;
        }
        return sum;
    }

    public static double rootSumSquaredDeviations(Collection<? extends Number> values) {
        return Math.sqrt(sumSquaredDeviations(values));
    }

    public static double skewness(Collection<? extends Number> values) {
        List<Double> sorted = values.stream().map(Number::doubleValue).sorted().toList();
        return 3.0 * (average(values) - medianSorted(sorted)) / sumSquaredDeviations(values);
    }

    public static double average(Collection<? extends Number> values) {
        if (values == null || values.isEmpty())
            return 0;
        double sum = 0;
        for (Number value : values) sum += value.doubleValue();
        return sum / values.size();
    }

    public static double kurtosis(Collection<? extends Number> values) {
        int size = values.size();
        if (size < 3)
            return 0;
        double sum = 0;
        for (Number value : values) sum += value.doubleValue();
        double mean = sum / size;
        double squaredDeviations = 0, fourthDeviations = 0;
        for (Number value : values) {
            double difference = mean - value.doubleValue();
            squaredDeviations += Math.pow(difference, 2);
            fourthDeviations += Math.pow(difference, 4);
        }
        double coefficient = (double) size * (size + 1.0) / ((size - 1.0) * (size - 2.0) * (size - 3.0));
        double correction = 3.0 * Math.pow(size - 1.0, 2) / ((size - 2.0) * (size - 3.0));
        // Click check thresholds use this interval-sum scale.
        return coefficient * (fourthDeviations / Math.pow(squaredDeviations / sum, 2)) - correction;
    }

    public static Number mode(Collection<? extends Number> values) {
        Map<Number, Integer> frequencies = new HashMap<>();
        values.forEach(value -> frequencies.merge(value, 1, Integer::sum));
        return frequencies.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow(NullPointerException::new).getKey();
    }

    public static Double mode(double[] values) {
        Map<Double, Integer> frequencies = new HashMap<>();
        Arrays.stream(values).forEach(value -> frequencies.merge(value, 1, Integer::sum));
        return frequencies.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow(NullPointerException::new).getKey();
    }

    public static int integerMode(Collection<Integer> values) {
        if (values.isEmpty())
            throw new IllegalArgumentException("Cannot find the mode of an empty collection");
        Map<Integer, Integer> frequencies = new LinkedHashMap<>();
        values.forEach(value -> frequencies.merge(value, 1, Integer::sum));
        return frequencies.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
    }

    private static double medianSorted(List<Double> values) {
        int middle = values.size() / 2;
        return values.size() % 2 == 0 ? (values.get(middle) + values.get(middle - 1)) / 2.0 : values.get(middle);
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException("Decimal places must be non-negative");
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static boolean isSmallScientificNotation(Number value) {
        return value.doubleValue() < 1.0 && Double.toString(value.doubleValue()).contains("E");
    }

    public static long approximateGcd(long first, long second) {
        while (second > 16384L) {
            long remainder = first % second;
            first = second;
            second = remainder;
        }
        return first;
    }

    public static double gcdWithTolerance(double first, double second) {
        if (first < second) {
            double swap = first;
            first = second;
            second = swap;
        }
        while (Math.abs(second) >= 0.001) {
            double remainder = first - Math.floor(first / second) * second;
            first = second;
            second = remainder;
        }
        return first;
    }

    public static double yawBetween(double firstX, double firstZ, double secondX, double secondZ) {
        double angle = Math.toDegrees(Math.atan2(firstZ - secondZ, secondX - firstX));
        return angle < 0 ? 360.0 + angle : angle;
    }

    public static double clicksPerSecond(Collection<? extends Number> intervals) {
        return 1000.0 / average(intervals);
    }

    public static int duplicateCount(Collection<?> values) {
        return values.size() - distinctCount(values);
    }

    public static int distinctCount(Collection<?> values) {
        return (int) values.stream().distinct().count();
    }

    public static int pingCompensationTicks(PlayerData player) {
        return FastMath.floor(player.getConfirmationTracker().getTransactionPingMillis() / 50.0) + 3;
    }
}
