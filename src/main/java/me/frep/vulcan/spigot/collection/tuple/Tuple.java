package me.frep.vulcan.spigot.collection.tuple;

public class Tuple {

    private final Object first;

    private final Object second;

    public Tuple(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Tuple && this.first.equals(((Tuple) other).first) && this.second.equals(((Tuple) other).second);
    }

    @Override
    public int hashCode() {
        return this.first.hashCode();
    }

    public static Object getFirst(Tuple tuple) {
        return tuple.first;
    }

    public static Object getSecond(Tuple tuple) {
        return tuple.second;
    }
}
