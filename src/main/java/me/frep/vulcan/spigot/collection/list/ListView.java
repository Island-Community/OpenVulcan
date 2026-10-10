package me.frep.vulcan.spigot.collection.list;

import java.util.List;

public abstract class ListView<E> implements List<E> {

    protected final List<E> delegate;

    public ListView(List<E> delegate) {
        this.delegate = delegate;
    }

    public List<E> getDelegate() {
        return delegate;
    }
}
