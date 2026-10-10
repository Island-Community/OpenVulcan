package me.frep.vulcan.spigot.alert;

@FunctionalInterface
public interface AlertHandler {

    void onChange(Object previousValue, Object currentValue);
}
