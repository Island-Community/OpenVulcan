package me.frep.vulcan.spigot.util.text.escaping;

import java.util.Arrays;

public class ArrayEntityMap implements EntityNameMap {

    protected final int growthStep;

    protected int size;

    protected String[] names;

    protected int[] codePoints;

    public ArrayEntityMap() {
        this(100);
    }

    public ArrayEntityMap(int initialCapacity) {
        growthStep = initialCapacity;
        names = new String[initialCapacity];
        codePoints = new int[initialCapacity];
    }

    protected void ensureCapacity(int required) {
        if (required <= names.length)
            return;
        int capacity = Math.max(required, size + growthStep);
        names = Arrays.copyOf(names, capacity);
        codePoints = Arrays.copyOf(codePoints, capacity);
    }

    public void addEntity(String name, int codePoint) {
        ensureCapacity(size + 1);
        names[size] = name;
        codePoints[size++] = codePoint;
    }

    public String nameFor(int codePoint) {
        for (int index = 0; index < size; index++) if (codePoints[index] == codePoint)
            return names[index];
        return null;
    }

    public int codePointFor(String name) {
        for (int index = 0; index < size; index++) if (names[index].equals(name))
            return codePoints[index];
        return -1;
    }
}
