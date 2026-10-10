package me.frep.vulcan.spigot.geometry.box;

import me.frep.vulcan.spigot.geometry.vector.PositionVector;
import me.frep.vulcan.spigot.geometry.direction.Direction;
import me.frep.vulcan.spigot.geometry.ray.LocationRayHit;
import java.util.LinkedList;
import java.util.function.Predicate;
import me.frep.vulcan.spigot.check.exemption.ConditionUtils;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public final class BoundingBox {

    private double minX;

    private double minY;

    private double minZ;

    private double maxX;

    private double maxY;

    private double maxZ;

    private final long createdAtMillis;

    public BoundingBox(double x, double y, double z) {
        this(x, x, y, y, z, z);
    }

    public BoundingBox(double firstX, double secondX, double firstY, double secondY, double firstZ, double secondZ) {
        createdAtMillis = System.currentTimeMillis();
        minX = Math.min(firstX, secondX);
        maxX = Math.max(firstX, secondX);
        minY = Math.min(firstY, secondY);
        maxY = Math.max(firstY, secondY);
        minZ = Math.min(firstZ, secondZ);
        maxZ = Math.max(firstZ, secondZ);
    }

    public double horizontalBoundaryDistance(Location location) {
        return Math.sqrt(Math.min(Math.pow(location.getX() - this.minX, 2.0), Math.pow(location.getX() - this.maxX, 2.0)) + Math.min(Math.pow(location.getZ() - this.minZ, 2.0), Math.pow(location.getZ() - this.maxZ, 2.0)));
    }

    public double horizontalBoundaryDistance(double x, double z) {
        double distanceXSquared = Math.min(Math.pow(x - this.minX, 2.0), Math.pow(x - this.maxX, 2.0));
        double distanceZSquared = Math.min(Math.pow(z - this.minZ, 2.0), Math.pow(z - this.maxZ, 2.0));
        return Math.sqrt(distanceXSquared + distanceZSquared);
    }

    public double horizontalBoundaryDistance(BoundingBox other) {
        double distanceXSquared = Math.min(Math.pow(other.minX - this.minX, 2.0), Math.pow(other.maxX - this.maxX, 2.0));
        double distanceZSquared = Math.min(Math.pow(other.minZ - this.minZ, 2.0), Math.pow(other.maxZ - this.maxZ, 2.0));
        return Math.sqrt(distanceXSquared + distanceZSquared);
    }

    public BoundingBox add(BoundingBox other) {
        this.minX = this.minX + other.minX;
        this.minY = this.minY + other.minY;
        this.minZ = this.minZ + other.minZ;
        this.maxX = this.maxX + other.maxX;
        this.maxY = this.maxY + other.maxY;
        this.maxZ = this.maxZ + other.maxZ;
        return this;
    }

    public BoundingBox offset(double deltaX, double deltaY, double deltaZ) {
        this.minX += deltaX;
        this.minY += deltaY;
        this.minZ += deltaZ;
        this.maxX += deltaX;
        this.maxY += deltaY;
        this.maxZ += deltaZ;
        return this;
    }

    public BoundingBox expand(double amountX, double amountY, double amountZ) {
        this.minX -= amountX;
        this.minY -= amountY;
        this.minZ -= amountZ;
        this.maxX += amountX;
        this.maxY += amountY;
        this.maxZ += amountZ;
        return this;
    }

    public boolean allBlocksMatch(World world, Predicate<? super Material> predicate) {
        int minimumBlockX = (int) Math.floor(this.minX);
        int maximumBlockX = (int) Math.ceil(this.maxX);
        int minimumBlockY = (int) Math.floor(this.minY);
        int maximumBlockY = (int) Math.ceil(this.maxY);
        int minimumBlockZ = (int) Math.floor(this.minZ);
        int maximumBlockZ = (int) Math.ceil(this.maxZ);
        LinkedList<Material> materials = new LinkedList<>();
        materials.add(BlockUtils.getMaterial(world, minimumBlockX, minimumBlockY, minimumBlockZ));
        for (int blockX = minimumBlockX; blockX < maximumBlockX; blockX++) {
            for (int blockY = minimumBlockY; blockY < maximumBlockY; blockY++) {
                for (int blockZ = minimumBlockZ; blockZ < maximumBlockZ; blockZ++) {
                    materials.add(BlockUtils.getMaterial(world, blockX, blockY, blockZ));
                }
            }
        }
        boolean allMatch = ConditionUtils.allMatch(materials, predicate);
        return allMatch;
    }

    public double centerX() {
        return (this.minX + this.maxX) / 2.0;
    }

    public double centerY() {
        return (this.minY + this.maxY) / 2.0;
    }

    public double centerZ() {
        return (this.minZ + this.maxZ) / 2.0;
    }

    public LocationRayHit rayTrace(PositionVector start, PositionVector end) {
        PositionVector[] candidates = { start.interpolateX(end, minX), start.interpolateX(end, maxX), start.interpolateY(end, minY), start.interpolateY(end, maxY), start.interpolateZ(end, minZ), start.interpolateZ(end, maxZ) };
        Direction[] faces = { Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
        PositionVector closest = null;
        Direction face = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (int index = 0; index < candidates.length; index++) {
            PositionVector point = candidates[index];
            if (point == null || point.getX() < minX || point.getX() > maxX || point.getY() < minY || point.getY() > maxY || point.getZ() < minZ || point.getZ() > maxZ)
                continue;
            double distance = start.distanceSquared(point);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = point;
                face = faces[index];
            }
        }
        return closest == null ? null : new LocationRayHit(closest, face);
    }

    public BoundingBox copy() {
        return new BoundingBox(this.minX, this.maxX, this.minY, this.maxY, this.minZ, this.maxZ);
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMinZ() {
        return this.minZ;
    }

    public double getMaxX() {
        return this.maxX;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public double getMaxZ() {
        return this.maxZ;
    }

    public long getCreatedAtMillis() {
        return this.createdAtMillis;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }
}
