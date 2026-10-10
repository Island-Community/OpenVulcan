package me.frep.vulcan.spigot.data;

public class RotationSample {

    private final double x;

    private final double y;

    private final double z;

    private boolean onGround = false;

    private boolean teleportInvalidated = false;

    private double deltaX = 0.0;

    private double deltaY = 0.0;

    private double deltaZ = 0.0;

    private double deltaXZ = 0.0;

    public RotationSample(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isTeleportInvalidated() {
        return this.teleportInvalidated;
    }

    public double getDeltaX() {
        return this.deltaX;
    }

    public double getDeltaY() {
        return this.deltaY;
    }

    public double getDeltaZ() {
        return this.deltaZ;
    }

    public double getDeltaXZ() {
        return this.deltaXZ;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setTeleportInvalidated(boolean teleportInvalidated) {
        this.teleportInvalidated = teleportInvalidated;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public void setDeltaZ(double deltaZ) {
        this.deltaZ = deltaZ;
    }

    public void setDeltaXZ(double deltaXZ) {
        this.deltaXZ = deltaXZ;
    }
}
