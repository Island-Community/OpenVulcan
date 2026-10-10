package me.frep.vulcan.spigot.geometry.direction;

import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public enum Orientation implements Predicate<Direction>, Iterable<Direction> {

    HORIZONTAL, VERTICAL;

    public Direction[] getDirections() {
        return this == HORIZONTAL ? new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST } : new Direction[] { Direction.UP, Direction.DOWN };
    }

    public Direction random(Random random) {
        Direction[] directions = getDirections();
        return directions[random.nextInt(directions.length)];
    }

    public boolean matches(Direction direction) {
        return direction != null && direction.getAxis().getOrientation() == this;
    }

    public boolean apply(Direction direction) {
        return matches(direction);
    }

    public Iterator<Direction> iterator() {
        return Arrays.asList(getDirections()).iterator();
    }
}
