package me.frep.vulcan.spigot.util.text.substitution;

import java.util.Map;
import me.frep.vulcan.spigot.util.text.substitution.MapStringLookup;

public abstract class StringLookup {

    private static final StringLookup NONE = new MapStringLookup(null);

    private static final StringLookup SYSTEM_PROPERTIES;

    public static StringLookup none() {
        return NONE;
    }

    public static StringLookup systemProperties() {
        return SYSTEM_PROPERTIES;
    }

    public static StringLookup forMap(Map<?, ?> map) {
        return new MapStringLookup(map);
    }

    protected StringLookup() {
    }

    public abstract String lookup(String name);

    static {
        SYSTEM_PROPERTIES = new MapStringLookup(System.getProperties());
    }
}
