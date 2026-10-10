package me.frep.vulcan.spigot.geometry.direction;

import com.google.common.base.Predicate;
import java.util.Locale;
import me.frep.vulcan.spigot.message.TextProvider;

public enum Axis implements Predicate<Direction>, TextProvider {

    X("x", Orientation.HORIZONTAL), Y("y", Orientation.VERTICAL), Z("z", Orientation.HORIZONTAL);

    private final String name;

    private final Orientation orientation;

    Axis(String name, Orientation orientation) {
        this.name = name;
        this.orientation = orientation;
    }

    public static Axis byName(String name) {
        if (name == null)
            return null;
        return switch(name.toLowerCase(Locale.ROOT)) {
            case "x" ->
                X;
            case "y" ->
                Y;
            case "z" ->
                Z;
            default ->
                null;
        };
    }

    public boolean isVertical() {
        return orientation == Orientation.VERTICAL;
    }

    public boolean isHorizontal() {
        return orientation == Orientation.HORIZONTAL;
    }

    public boolean matches(Direction direction) {
        return direction != null && direction.getAxis() == this;
    }

    public boolean apply(Direction direction) {
        return matches(direction);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return name;
    }

    public String toString() {
        return name;
    }
}
