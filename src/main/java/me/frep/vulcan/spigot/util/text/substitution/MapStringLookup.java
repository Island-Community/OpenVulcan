package me.frep.vulcan.spigot.util.text.substitution;

import java.util.Map;
import me.frep.vulcan.spigot.util.text.substitution.StringLookup;

public class MapStringLookup extends StringLookup {

    private final Map<?, ?> values;

    public MapStringLookup(Map<?, ?> values) {
        this.values = values;
    }

    public String lookup(String name) {
        if (values == null)
            return null;
        Object value = values.get(name);
        return value == null ? null : value.toString();
    }
}
