package me.frep.vulcan.spigot.geometry.position;

import java.util.Iterator;

public final class MutableBlockPositionRange implements Iterable<MutableBlockPosition> {

    private final BlockPosition minimum;

    private final BlockPosition maximum;

    public MutableBlockPositionRange(BlockPosition minimum, BlockPosition maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Iterator<MutableBlockPosition> iterator() {
        return new MutableBlockPositionIterator(minimum, maximum);
    }
}
