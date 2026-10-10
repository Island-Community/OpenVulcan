package me.frep.vulcan.spigot.util.text.escaping;

public interface EntityNameMap {

    void addEntity(String name, int codePoint);

    String nameFor(int codePoint);

    int codePointFor(String name);
}
