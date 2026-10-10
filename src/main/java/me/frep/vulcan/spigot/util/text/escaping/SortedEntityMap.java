package me.frep.vulcan.spigot.util.text.escaping;

import java.util.Arrays;

public class SortedEntityMap extends ArrayEntityMap {

    public SortedEntityMap() {
    }

    public SortedEntityMap(int initialCapacity) {
        super(initialCapacity);
    }

    public void addEntity(String name, int codePoint) {
        int index = Arrays.binarySearch(codePoints, 0, size, codePoint);
        if (index >= 0)
            return;
        index = -index - 1;
        ensureCapacity(size + 1);
        System.arraycopy(codePoints, index, codePoints, index + 1, size - index);
        System.arraycopy(names, index, names, index + 1, size - index);
        codePoints[index] = codePoint;
        names[index] = name;
        size++;
    }

    public String nameFor(int codePoint) {
        int index = Arrays.binarySearch(codePoints, 0, size, codePoint);
        return index < 0 ? null : names[index];
    }
}
