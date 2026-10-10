package me.frep.vulcan.spigot.geometry.direction;

public enum Polarity {

    POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

    private final int sign;

    private final String description;

    Polarity(int sign, String description) {
        this.sign = sign;
        this.description = description;
    }

    public int getSign() {
        return sign;
    }

    public String toString() {
        return description;
    }
}
