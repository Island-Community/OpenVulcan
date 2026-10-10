package me.frep.vulcan.spigot.util.text.escaping;

import java.util.Map;

public abstract class BidirectionalEntityMap implements EntityNameMap {

    private final Map<String, Integer> valuesByName;

    private final Map<Integer, String> namesByValue;

    protected BidirectionalEntityMap(Map<String, Integer> valuesByName, Map<Integer, String> namesByValue) {
        this.valuesByName = valuesByName;
        this.namesByValue = namesByValue;
    }

    public void addEntity(String name, int codePoint) {
        valuesByName.put(name, codePoint);
        namesByValue.put(codePoint, name);
    }

    public String nameFor(int codePoint) {
        return namesByValue.get(codePoint);
    }

    public int codePointFor(String name) {
        return valuesByName.getOrDefault(name, -1);
    }
}
