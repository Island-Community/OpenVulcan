package me.frep.vulcan.spigot.geometry.vector;

import me.frep.vulcan.spigot.util.numbers.FastMath;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class PositionVector {

    private final double x;

    private final double y;

    private final double z;

    public PositionVector(double x, double y, double z) {
        this.x = x == 0.0 ? 0.0 : x;
        this.y = y == 0.0 ? 0.0 : y;
        this.z = z == 0.0 ? 0.0 : z;
    }

    public PositionVector vectorTo(PositionVector other) {
        return other.subtract(this);
    }

    public PositionVector normalize() {
        double length = length();
        return length < 1.0E-4 ? new PositionVector(0, 0, 0) : new PositionVector(x / length, y / length, z / length);
    }

    public double dot(PositionVector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public PositionVector cross(PositionVector other) {
        return new PositionVector(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public PositionVector subtract(PositionVector other) {
        return subtract(other.x, other.y, other.z);
    }

    public PositionVector subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    public PositionVector add(PositionVector other) {
        return add(other.x, other.y, other.z);
    }

    public PositionVector add(double x, double y, double z) {
        return new PositionVector(this.x + x, this.y + y, this.z + z);
    }

    public double distance(PositionVector other) {
        return FastMath.sqrt(distanceSquared(other));
    }

    public double distanceSquared(PositionVector other) {
        double dx = other.x - x, dy = other.y - y, dz = other.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length() {
        return FastMath.sqrt(x * x + y * y + z * z);
    }

    public PositionVector interpolateX(PositionVector end, double plane) {
        double delta = end.x - x;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - x) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new PositionVector(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public PositionVector interpolateY(PositionVector end, double plane) {
        double delta = end.y - y;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - y) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new PositionVector(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public PositionVector interpolateZ(PositionVector end, double plane) {
        double delta = end.z - z;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - z) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new PositionVector(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public PositionVector rotateX(float radians) {
        float cosine = FastMath.cos(radians), sine = FastMath.sin(radians);
        return new PositionVector(x, y * cosine + z * sine, z * cosine - y * sine);
    }

    public PositionVector rotateY(float radians) {
        float cosine = FastMath.cos(radians), sine = FastMath.sin(radians);
        return new PositionVector(x * cosine + z * sine, y, z * cosine - x * sine);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public PositionVector(PositionVector other) {
        this(other.x, other.y, other.z);
    }

    public PositionVector(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public double horizontalDistance(PositionVector other) {
        return FastMath.sqrt(horizontalDistanceSquared(other));
    }

    public double horizontalDistanceSquared(PositionVector other) {
        double dx = other.x - x, dz = other.z - z;
        return dx * dx + dz * dz;
    }

    public double verticalDistanceTo(PositionVector other) {
        return other.y - y;
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double horizontalLengthSquared() {
        return x * x + z * z;
    }

    public PositionVector offset(BlockFace face) {
        return add(face.getModX(), face.getModY(), face.getModZ());
    }

    public Vector toBukkitVector() {
        return new Vector(x, y, z);
    }

    public int getBlockX() {
        return FastMath.floor(x);
    }

    public int getBlockY() {
        return FastMath.floor(y);
    }

    public int getBlockZ() {
        return FastMath.floor(z);
    }

    public PositionVector multiply(double factor) {
        return multiply(factor, factor, factor);
    }

    public PositionVector multiply(double x, double y, double z) {
        return new PositionVector(this.x * x, this.y * y, this.z * z);
    }

    public double dotProduct(PositionVector other) {
        return dot(other);
    }

    public float angle(PositionVector other) {
        double cosine = dot(other) / (length() * other.length());
        return (float) Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
