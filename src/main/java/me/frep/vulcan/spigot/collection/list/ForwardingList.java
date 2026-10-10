package me.frep.vulcan.spigot.collection.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ForwardingList<E> extends ListView<E> {

    public ForwardingList(List<E> delegate) {
        super(delegate);
    }

    public abstract void beforeIteration();

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean contains(Object value) {
        return delegate.contains(value);
    }

    public Iterator<E> iterator() {
        beforeIteration();
        return listIterator();
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] array) {
        return delegate.toArray(array);
    }

    public boolean add(E value) {
        return delegate.add(value);
    }

    public boolean remove(Object value) {
        return delegate.remove(value);
    }

    public boolean addAll(Collection<? extends E> values) {
        return delegate.addAll(values);
    }

    public boolean addAll(int index, Collection<? extends E> values) {
        return delegate.addAll(index, values);
    }

    public void clear() {
        delegate.clear();
    }

    public E get(int index) {
        return delegate.get(index);
    }

    public E set(int index, E value) {
        return delegate.set(index, value);
    }

    public void add(int index, E value) {
        delegate.add(index, value);
    }

    public E remove(int index) {
        return delegate.remove(index);
    }

    public int indexOf(Object value) {
        return delegate.indexOf(value);
    }

    public int lastIndexOf(Object value) {
        return delegate.lastIndexOf(value);
    }

    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public List<E> subList(int from, int to) {
        return delegate.subList(from, to);
    }

    public boolean retainAll(Collection<?> values) {
        return delegate.retainAll(values);
    }

    public boolean removeAll(Collection<?> values) {
        return delegate.removeAll(values);
    }

    public boolean containsAll(Collection<?> values) {
        return delegate.containsAll(values);
    }
}
