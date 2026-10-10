package me.frep.vulcan.spigot.data;

import java.util.HashSet;
import java.util.Set;
import me.frep.vulcan.spigot.alert.AlertHandler;

public final class ObservableValue {

    private final Set<AlertHandler> observers = new HashSet<>();

    private Object value;

    public ObservableValue(Object initialValue) {
        this.value = initialValue;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object currentValue) {
        Object previousValue = this.value;
        this.value = currentValue;
        this.observers.forEach(observer -> observer.onChange(previousValue, currentValue));
    }

    public AlertHandler subscribe(AlertHandler observer) {
        this.observers.add(observer);
        return observer;
    }

    public void unsubscribe(AlertHandler observer) {
        this.observers.remove(observer);
    }
}
