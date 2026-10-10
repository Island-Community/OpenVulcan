package me.frep.vulcan.spigot.geometry.box;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;

public enum EntitySize {

    HAPPY_GHAST(EntityTypes.HAPPY_GHAST, 4.0, 4.0), DEFAULT(null, 0.6, 1.8);

    private final EntityType entityType;

    private final double width;

    private final double height;

    private EntitySize(final EntityType entityType, final double width, final double height) {
        this.entityType = entityType;
        this.width = width;
        this.height = height;
    }

    public AxisAlignedBox getAxisAlignedBox(double x, double y, double z) {
        double halfWidth = this.width / 2.0;
        return new AxisAlignedBox(x - halfWidth, y, z - halfWidth, x + halfWidth, y + this.height, z + halfWidth);
    }

    public static EntitySize getEntitySize(EntityType entityType) {
        for (EntitySize size : values()) {
            if (size.entityType != null && size.entityType.equals(entityType)) {
                return size;
            }
        }
        return DEFAULT;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
