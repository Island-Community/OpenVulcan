package me.frep.vulcan.spigot.collection.tuple;

public final class Pair {

    private Object first;

    private Object second;

    public Object getFirst() {
        return this.first;
    }

    public Object getSecond() {
        return this.second;
    }

    public void setFirst(Object first) {
        this.first = first;
    }

    public void setSecond(Object second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Pair otherPair)) {
            return false;
        }
        if (this.first == null ? otherPair.first != null : !this.first.equals(otherPair.first)) {
            return false;
        }
        return this.second == null ? otherPair.second == null : this.second.equals(otherPair.second);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        Object firstValue = this.getFirst();
        hash = hash * 59 + (firstValue == null ? 43 : firstValue.hashCode());
        Object secondValue = this.getSecond();
        return hash * 59 + (secondValue == null ? 43 : secondValue.hashCode());
    }

    @Override
    public String toString() {
        return "Pair(x=" + this.first + ", y=" + this.second + ")";
    }

    public Pair(Object first, Object second) {
        this.first = first;
        this.second = second;
    }
}
