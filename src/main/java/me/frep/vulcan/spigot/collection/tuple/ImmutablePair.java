package me.frep.vulcan.spigot.collection.tuple;

public class ImmutablePair {

    private final Object left;

    private final Object right;

    private static final long serialVersionUID = 105909838770267L;

    public ImmutablePair(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return !(other instanceof ImmutablePair otherPair) ? false : (this.left == null ? otherPair.left == null : this.left.equals(otherPair.left)) && (this.right == null ? otherPair.right == null : this.right.equals(otherPair.right));
    }

    @Override
    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) + (this.right == null ? 0 : this.right.hashCode());
    }

    @Override
    public String toString() {
        return "[" + this.left + ':' + this.right + ']';
    }
}
