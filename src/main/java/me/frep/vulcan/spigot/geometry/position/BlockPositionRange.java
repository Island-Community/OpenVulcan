package me.frep.vulcan.spigot.geometry.position;

import java.util.Iterator;

public final class BlockPositionRange implements Iterable<BlockPosition> {

    private final BlockPosition minimum;

    private final BlockPosition maximum;

    public BlockPositionRange(BlockPosition minimum, BlockPosition maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Iterator<BlockPosition> iterator() {
        return new BlockPositionIterator(minimum, maximum);
    }
}
