package me.frep.vulcan.spigot.geometry.direction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import me.frep.vulcan.spigot.geometry.position.GridPosition;
import me.frep.vulcan.spigot.message.TextProvider;

public enum Direction implements TextProvider {

    DOWN(0, 1, -1, "down", Polarity.NEGATIVE, Axis.Y, new GridPosition(0, -1, 0)),
    UP(1, 0, -1, "up", Polarity.POSITIVE, Axis.Y, new GridPosition(0, 1, 0)),
    NORTH(2, 3, 2, "north", Polarity.NEGATIVE, Axis.Z, new GridPosition(0, 0, -1)),
    SOUTH(3, 2, 0, "south", Polarity.POSITIVE, Axis.Z, new GridPosition(0, 0, 1)),
    WEST(4, 5, 1, "west", Polarity.NEGATIVE, Axis.X, new GridPosition(-1, 0, 0)),
    EAST(5, 4, 3, "east", Polarity.POSITIVE, Axis.X, new GridPosition(1, 0, 0));

    private static final Direction[] BY_INDEX = values();

    private static final Direction[] BY_HORIZONTAL_INDEX = { SOUTH, WEST, NORTH, EAST };

    private static final Map<String, Direction> BY_NAME = new HashMap<>();

    static {
        for (Direction direction : values()) BY_NAME.put(direction.name, direction);
    }

    private final int index;

    private final int oppositeIndex;

    private final int horizontalIndex;

    private final String name;

    private final Polarity polarity;

    private final Axis axis;

    private final GridPosition gridPosition;

    Direction(int index, int oppositeIndex, int horizontalIndex, String name, Polarity polarity, Axis axis, GridPosition gridPosition) {
        this.index = index;
        this.oppositeIndex = oppositeIndex;
        this.horizontalIndex = horizontalIndex;
        this.name = name;
        this.polarity = polarity;
        this.axis = axis;
        this.gridPosition = gridPosition;
    }

    public int getIndex() {
        return index;
    }

    public int getHorizontalIndex() {
        return horizontalIndex;
    }

    public Direction getOpposite() {
        return BY_INDEX[oppositeIndex];
    }

    public Polarity getPolarity() {
        return polarity;
    }

    public Axis getAxis() {
        return axis;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public int getOffsetX() {
        return axis == Axis.X ? polarity.getSign() : 0;
    }

    public int getOffsetY() {
        return axis == Axis.Y ? polarity.getSign() : 0;
    }

    public int getOffsetZ() {
        return axis == Axis.Z ? polarity.getSign() : 0;
    }

    public String getText() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public Direction rotateClockwise(Axis rotationAxis) {
        if (axis == rotationAxis)
            return this;
        return switch(rotationAxis) {
            case X ->
                rotateX();
            case Y ->
                rotateY();
            case Z ->
                rotateZ();
        };
    }

    public Direction rotateY() {
        return switch(this) {
            case NORTH ->
                EAST;
            case EAST ->
                SOUTH;
            case SOUTH ->
                WEST;
            case WEST ->
                NORTH;
            default ->
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    private Direction rotateX() {
        return switch(this) {
            case NORTH ->
                DOWN;
            case SOUTH ->
                UP;
            case UP ->
                NORTH;
            case DOWN ->
                SOUTH;
            default ->
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction rotateZ() {
        return switch(this) {
            case EAST ->
                DOWN;
            case WEST ->
                UP;
            case UP ->
                EAST;
            case DOWN ->
                WEST;
            default ->
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    public Direction rotateYCounterclockwise() {
        return switch(this) {
            case NORTH ->
                WEST;
            case EAST ->
                NORTH;
            case SOUTH ->
                EAST;
            case WEST ->
                SOUTH;
            default ->
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public static Direction byName(String name) {
        return name == null ? null : BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public static Direction byIndex(int index) {
        return BY_INDEX[Math.abs(index % BY_INDEX.length)];
    }

    public static Direction byHorizontalIndex(int index) {
        return BY_HORIZONTAL_INDEX[Math.abs(index % BY_HORIZONTAL_INDEX.length)];
    }

    public static Direction fromYaw(double yaw) {
        return byHorizontalIndex((int) Math.floor(yaw / 90.0 + 0.5) & 3);
    }

    public static Direction random(Random random) {
        return BY_INDEX[random.nextInt(BY_INDEX.length)];
    }

    public static Direction fromVector(float x, float y, float z) {
        Direction closest = NORTH;
        float bestDot = Float.MIN_VALUE;
        for (Direction direction : values()) {
            float dot = x * direction.getOffsetX() + y * direction.getOffsetY() + z * direction.getOffsetZ();
            if (dot > bestDot) {
                bestDot = dot;
                closest = direction;
            }
        }
        return closest;
    }
}
