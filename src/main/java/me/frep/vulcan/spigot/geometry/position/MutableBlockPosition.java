package me.frep.vulcan.spigot.geometry.position;

public final class MutableBlockPosition extends BlockPosition {

    public int x;

    public int y;

    public int z;

    public MutableBlockPosition(int x, int y, int z) {
        super(0, 0, 0);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }
}
