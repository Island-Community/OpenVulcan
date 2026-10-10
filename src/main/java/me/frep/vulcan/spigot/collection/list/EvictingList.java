package me.frep.vulcan.spigot.collection.list;

import java.util.Collection;
import java.util.LinkedList;

public final class EvictingList<E> extends LinkedList<E> {

    private final int capacity;

    public EvictingList(int capacity) {
        this.capacity = capacity;
    }

    public EvictingList(Collection<? extends E> elements, int capacity) {
        super(elements);
        this.capacity = capacity;
    }

    @Override
    public boolean add(E element) {
        if (this.size() >= this.getCapacity()) {
            this.removeFirst();
        }
        return super.add(element);
    }

    public boolean isFull() {
        return this.size() >= this.getCapacity();
    }

    public int getCapacity() {
        return this.capacity;
    }
}
