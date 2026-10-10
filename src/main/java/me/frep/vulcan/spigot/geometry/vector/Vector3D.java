package me.frep.vulcan.spigot.geometry.vector;

import me.frep.vulcan.spigot.util.numbers.FastMath;

public class Vector3D {

    public final double x;

    public final double y;

    public final double z;

    public Vector3D(double x, double y, double z) {
        this.x = x == 0.0 ? 0.0 : x;
        this.y = y == 0.0 ? 0.0 : y;
        this.z = z == 0.0 ? 0.0 : z;
    }

    public Vector3D vectorTo(Vector3D other) {
        return other.subtract(this);
    }

    public Vector3D normalize() {
        double length = length();
        return length < 1.0E-4 ? new Vector3D(0, 0, 0) : new Vector3D(x / length, y / length, z / length);
    }

    public double dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3D cross(Vector3D other) {
        return new Vector3D(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public Vector3D subtract(Vector3D other) {
        return subtract(other.x, other.y, other.z);
    }

    public Vector3D subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    public Vector3D add(Vector3D other) {
        return add(other.x, other.y, other.z);
    }

    public Vector3D add(double x, double y, double z) {
        return new Vector3D(this.x + x, this.y + y, this.z + z);
    }

    public double distance(Vector3D other) {
        return FastMath.sqrt(distanceSquared(other));
    }

    public double distanceSquared(Vector3D other) {
        double dx = other.x - x, dy = other.y - y, dz = other.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length() {
        return FastMath.sqrt(x * x + y * y + z * z);
    }

    public Vector3D interpolateX(Vector3D end, double plane) {
        double delta = end.x - x;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - x) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new Vector3D(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public Vector3D interpolateY(Vector3D end, double plane) {
        double delta = end.y - y;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - y) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new Vector3D(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public Vector3D interpolateZ(Vector3D end, double plane) {
        double delta = end.z - z;
        if (delta * delta < 1.0E-7F)
            return null;
        double fraction = (plane - z) / delta;
        return fraction >= 0.0 && fraction <= 1.0 ? new Vector3D(x + (end.x - x) * fraction, y + (end.y - y) * fraction, z + (end.z - z) * fraction) : null;
    }

    public Vector3D rotateX(float radians) {
        float cosine = FastMath.cos(radians), sine = FastMath.sin(radians);
        return new Vector3D(x, y * cosine + z * sine, z * cosine - y * sine);
    }

    public Vector3D rotateY(float radians) {
        float cosine = FastMath.cos(radians), sine = FastMath.sin(radians);
        return new Vector3D(x * cosine + z * sine, y, z * cosine - x * sine);
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

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
