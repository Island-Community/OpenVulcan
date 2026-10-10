package me.frep.vulcan.spigot.util.text.escaping;

public class CachedEntityMap extends HashEntityMap {

    private String[] cachedNames;

    public void addEntity(String name, int codePoint) {
        super.addEntity(name, codePoint);
        cachedNames = null;
    }

    public String nameFor(int codePoint) {
        if (codePoint < 0 || codePoint >= 256)
            return super.nameFor(codePoint);
        if (cachedNames == null) {
            cachedNames = new String[256];
            for (int value = 0; value < cachedNames.length; value++) cachedNames[value] = super.nameFor(value);
        }
        return cachedNames[codePoint];
    }
}
