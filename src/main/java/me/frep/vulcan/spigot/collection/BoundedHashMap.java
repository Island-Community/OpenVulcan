package me.frep.vulcan.spigot.collection;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class BoundedHashMap<K, V> extends HashMap<K, V> {

    private final int capacity;

    private final Deque<K> insertionOrder = new LinkedList<>();

    @Override
    public boolean remove(Object key, Object value) {
        this.insertionOrder.remove(key);
        return super.remove(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!this.insertionOrder.contains(key) || !this.get(key).equals(value)) {
            this.evictOldestIfFull();
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        this.evictOldestIfFull();
        this.insertionOrder.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        entries.forEach(this::put);
    }

    @Override
    public void clear() {
        this.insertionOrder.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        this.insertionOrder.remove(key);
        return super.remove(key);
    }

    private boolean evictOldestIfFull() {
        if (!(this.insertionOrder.size() >= this.capacity)) {
            return false;
        }
        K oldestKey = this.insertionOrder.removeFirst();
        this.remove(oldestKey);
        return true;
    }

    public BoundedHashMap(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return this.capacity;
    }
}
