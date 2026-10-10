package me.frep.vulcan.spigot.geometry.position;

import com.google.common.collect.AbstractIterator;

final class MutableBlockPositionIterator extends AbstractIterator<MutableBlockPosition> {

    private final BlockPosition minimum;

    private final BlockPosition maximum;

    private MutableBlockPosition current;

    MutableBlockPositionIterator(BlockPosition minimum, BlockPosition maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    protected MutableBlockPosition computeNext() {
        if (current == null) {
            if (minimum.getX() > maximum.getX() || minimum.getY() > maximum.getY() || minimum.getZ() > maximum.getZ())
                return endOfData();
            current = new MutableBlockPosition(minimum.getX(), minimum.getY(), minimum.getZ());
            return current;
        }
        if (current.equals(maximum))
            return endOfData();
        int x = current.getX(), y = current.getY(), z = current.getZ();
        if (x < maximum.getX())
            x++;
        else if (y < maximum.getY()) {
            x = minimum.getX();
            y++;
        } else {
            x = minimum.getX();
            y = minimum.getY();
            z++;
        }
        current.x = x;
        current.y = y;
        current.z = z;
        return current;
    }
}
