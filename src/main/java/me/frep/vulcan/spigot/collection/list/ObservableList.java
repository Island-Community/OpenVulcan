package me.frep.vulcan.spigot.collection.list;

import java.util.List;

public class ObservableList<E> extends ForwardingList<E> {

    private final Runnable onIteration;

    public ObservableList(List<E> delegate, Runnable onIteration) {
        super(delegate);
        this.onIteration = onIteration;
    }

    public void beforeIteration() {
        onIteration.run();
    }
}
