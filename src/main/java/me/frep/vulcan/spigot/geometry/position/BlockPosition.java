package me.frep.vulcan.spigot.geometry.position;

import me.frep.vulcan.spigot.geometry.direction.Direction;
import me.frep.vulcan.spigot.geometry.vector.Vector3D;
import org.bukkit.entity.Entity;

public class BlockPosition extends GridPosition {

    public static final BlockPosition ORIGIN = new BlockPosition(0, 0, 0);

    private static final int X_BITS = 26;

    private static final int Z_BITS = 26;

    private static final int Y_BITS = 12;

    private static final int Y_SHIFT = Z_BITS;

    private static final int X_SHIFT = Y_SHIFT + Y_BITS;

    private static final long X_MASK = (1L << X_BITS) - 1;

    private static final long Y_MASK = (1L << Y_BITS) - 1;

    private static final long Z_MASK = (1L << Z_BITS) - 1;

    public BlockPosition(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPosition(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPosition(Entity entity) {
        this(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
    }

    public BlockPosition(Vector3D vector) {
        this(vector.x, vector.y, vector.z);
    }

    public BlockPosition(GridPosition position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public BlockPosition add(double x, double y, double z) {
        return new BlockPosition(getX() + x, getY() + y, getZ() + z);
    }

    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(getX() + x, getY() + y, getZ() + z);
    }

    public BlockPosition add(GridPosition other) {
        return add(other.getX(), other.getY(), other.getZ());
    }

    public BlockPosition subtract(GridPosition other) {
        return add(-other.getX(), -other.getY(), -other.getZ());
    }

    public BlockPosition multiply(int factor) {
        return new BlockPosition(getX() * factor, getY() * factor, getZ() * factor);
    }

    public BlockPosition up() {
        return up(1);
    }

    public BlockPosition up(int distance) {
        return offset(Direction.UP, distance);
    }

    public BlockPosition down() {
        return down(1);
    }

    public BlockPosition down(int distance) {
        return offset(Direction.DOWN, distance);
    }

    public BlockPosition north() {
        return north(1);
    }

    public BlockPosition north(int distance) {
        return offset(Direction.NORTH, distance);
    }

    public BlockPosition south() {
        return south(1);
    }

    public BlockPosition south(int distance) {
        return offset(Direction.SOUTH, distance);
    }

    public BlockPosition west() {
        return west(1);
    }

    public BlockPosition west(int distance) {
        return offset(Direction.WEST, distance);
    }

    public BlockPosition east() {
        return east(1);
    }

    public BlockPosition east(int distance) {
        return offset(Direction.EAST, distance);
    }

    public BlockPosition offset(Direction direction) {
        return offset(direction, 1);
    }

    public BlockPosition offset(Direction direction, int distance) {
        return add(direction.getOffsetX() * distance, direction.getOffsetY() * distance, direction.getOffsetZ() * distance);
    }

    public BlockPosition crossProduct(GridPosition other) {
        return cross(other);
    }

    @Override
    public BlockPosition cross(GridPosition other) {
        return new BlockPosition(getY() * other.getZ() - getZ() * other.getY(), getZ() * other.getX() - getX() * other.getZ(), getX() * other.getY() - getY() * other.getX());
    }

    public long pack() {
        return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | getZ() & Z_MASK;
    }

    public static Iterable<BlockPosition> positionsBetween(BlockPosition first, BlockPosition second) {
        return new BlockPositionRange(minimum(first, second), maximum(first, second));
    }

    public static Iterable<MutableBlockPosition> mutablePositionsBetween(BlockPosition first, BlockPosition second) {
        return new MutableBlockPositionRange(minimum(first, second), maximum(first, second));
    }

    private static BlockPosition minimum(BlockPosition first, BlockPosition second) {
        return new BlockPosition(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()));
    }

    private static BlockPosition maximum(BlockPosition first, BlockPosition second) {
        return new BlockPosition(Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
    }
}
