package me.frep.vulcan.spigot.util.numbers;

import java.util.Random;
import java.util.UUID;
import me.frep.vulcan.spigot.geometry.position.GridPosition;

public final class FastMath {

    public static final float SQRT_TWO = sqrt(2.0F);

    private static final float[] SIN_TABLE = new float[65536];

    private static final int[] DE_BRUIJN_BITS = { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };

    static {
        for (int index = 0; index < SIN_TABLE.length; index++) SIN_TABLE[index] = (float) Math.sin(index * Math.PI * 2.0 / SIN_TABLE.length);
    }

    private FastMath() {
    }

    public static float sin(float radians) {
        return SIN_TABLE[(int) (radians * 10430.378F) & 65535];
    }

    public static float cos(float radians) {
        return SIN_TABLE[(int) (radians * 10430.378F + 16384.0F) & 65535];
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static float sqrt(double value) {
        return (float) Math.sqrt(value);
    }

    public static int floor(float value) {
        int integer = (int) value;
        return value < integer ? integer - 1 : integer;
    }

    public static int floor(double value) {
        int integer = (int) value;
        return value < integer ? integer - 1 : integer;
    }

    public static int fastFloor(double value) {
        return (int) (value + 1024.0) - 1024;
    }

    public static long floorToLong(double value) {
        long integer = (long) value;
        return value < integer ? integer - 1 : integer;
    }

    public static int absFloor(double value) {
        return (int) (value >= 0.0 ? value : -value + 1.0);
    }

    public static float abs(float value) {
        return value >= 0 ? value : -value;
    }

    public static int abs(int value) {
        return value >= 0 ? value : -value;
    }

    public static int ceil(float value) {
        int integer = (int) value;
        return value > integer ? integer + 1 : integer;
    }

    public static int ceil(double value) {
        int integer = (int) value;
        return value > integer ? integer + 1 : integer;
    }

    public static int clamp(int value, int minimum, int maximum) {
        return value < minimum ? minimum : Math.min(value, maximum);
    }

    public static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }

    public static double clamp(double value, double minimum, double maximum) {
        return value < minimum ? minimum : (value > maximum ? maximum : value);
    }

    public static double clampedLerp(double start, double end, double fraction) {
        return fraction < 0 ? start : (fraction > 1 ? end : start + (end - start) * fraction);
    }

    public static double maxAbs(double first, double second) {
        return Math.max(Math.abs(first), Math.abs(second));
    }

    public static int floorDiv(int value, int divisor) {
        return Math.floorDiv(value, divisor);
    }

    public static int randomBetween(Random random, int minimum, int maximum) {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static float randomBetween(Random random, float minimum, float maximum) {
        return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
    }

    public static double randomBetween(Random random, double minimum, double maximum) {
        return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
    }

    public static double average(long[] values) {
        long sum = 0;
        for (long value : values) sum += value;
        return (double) sum / values.length;
    }

    public static boolean approximatelyEqual(float first, float second) {
        return abs(second - first) < 1.0E-5F;
    }

    public static int positiveModulo(int value, int divisor) {
        return (value % divisor + divisor) % divisor;
    }

    public static float wrapDegrees(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F)
            angle -= 360.0F;
        if (angle < -180.0F)
            angle += 360.0F;
        return angle;
    }

    public static double wrapDegrees(double angle) {
        angle %= 360.0;
        if (angle >= 180.0)
            angle -= 360.0;
        if (angle < -180.0)
            angle += 360.0;
        return angle;
    }

    public static int parseInt(String value) {
        return Integer.parseInt(value);
    }

    public static int parseIntAtLeast(String value, int minimum) {
        return Math.max(minimum, parseInt(value));
    }

    public static double parseDoubleOrDefault(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static double parseDoubleAtLeast(String value, double fallback, double minimum) {
        return Math.max(minimum, parseDoubleOrDefault(value, fallback));
    }

    public static int nextPowerOfTwo(int value) {
        int result = value - 1;
        result |= result >> 1;
        result |= result >> 2;
        result |= result >> 4;
        result |= result >> 8;
        result |= result >> 16;
        return result + 1;
    }

    private static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    private static int ceilLog2(int value) {
        int power = isPowerOfTwo(value) ? value : nextPowerOfTwo(value);
        return DE_BRUIJN_BITS[(int) (power * 125613361L >> 27) & 31];
    }

    public static int floorLog2(int value) {
        return ceilLog2(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public static int roundUpToMultiple(int value, int multiple) {
        if (multiple == 0)
            return 0;
        if (value == 0)
            return multiple;
        if (value < 0)
            multiple *= -1;
        int remainder = value % multiple;
        return remainder == 0 ? value : value + multiple - remainder;
    }

    public static int packRgb(float red, float green, float blue) {
        return packRgb(floor(red * 255.0F), floor(green * 255.0F), floor(blue * 255.0F));
    }

    public static int packRgb(int red, int green, int blue) {
        return ((red << 8) + green << 8) + blue;
    }

    public static int multiplyColors(int first, int second) {
        int red = (int) ((float) ((first >> 16) & 255) * ((second >> 16) & 255) / 255.0F);
        int green = (int) ((float) ((first >> 8) & 255) * ((second >> 8) & 255) / 255.0F);
        int blue = (int) ((float) (first & 255) * (second & 255) / 255.0F);
        return first & 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public static long coordinateHash(GridPosition position) {
        return coordinateHash(position.getX(), position.getY(), position.getZ());
    }

    public static long coordinateHash(int x, int y, int z) {
        long hash = x * 3129871 ^ z * 116129781L ^ y;
        return hash * hash * 42317861L + hash * 11L;
    }

    public static UUID randomUuid(Random random) {
        return new UUID(random.nextLong() & -61441L | 16384L, random.nextLong() & 4611686018427387903L | Long.MIN_VALUE);
    }
}
