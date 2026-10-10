package me.frep.vulcan.spigot.geometry.position;

import me.frep.vulcan.spigot.geometry.vector.Vector3D;
import me.frep.vulcan.spigot.util.numbers.FastMath;

public class GridPosition implements Comparable<GridPosition> {

    public static final GridPosition ORIGIN = new GridPosition(0, 0, 0);

    private final int x;

    private final int y;

    private final int z;

    public GridPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GridPosition(double x, double y, double z) {
        this(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object value) {
        return value instanceof GridPosition other && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return (getY() + getZ() * 31) * 31 + getX();
    }

    public int compareCoordinates(GridPosition other) {
        return compareTo(other);
    }

    @Override
    public int compareTo(GridPosition other) {
        int comparison = Integer.compare(getY(), other.getY());
        if (comparison == 0)
            comparison = Integer.compare(getZ(), other.getZ());
        if (comparison == 0)
            comparison = Integer.compare(getX(), other.getX());
        return comparison;
    }

    public GridPosition cross(GridPosition other) {
        return new GridPosition(getY() * other.getZ() - getZ() * other.getY(), getZ() * other.getX() - getX() * other.getZ(), getX() * other.getY() - getY() * other.getX());
    }

    public double distanceSquared(double x, double y, double z) {
        double dx = getX() - x, dy = getY() - y, dz = getZ() - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double centerDistanceSquared(double x, double y, double z) {
        double dx = getX() + 0.5 - x, dy = getY() + 0.5 - y, dz = getZ() + 0.5 - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distanceSquared(Vector3D point) {
        return distanceSquared(point.x, point.y, point.z);
    }
}
