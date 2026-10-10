package me.frep.vulcan.spigot.geometry.ray;

import me.frep.vulcan.spigot.geometry.vector.PositionVector;
import me.frep.vulcan.spigot.geometry.direction.Direction;
import me.frep.vulcan.spigot.geometry.position.BlockPosition;
import org.bukkit.entity.Entity;

public class LocationRayHit {

    private BlockPosition blockPosition;

    public RayHitType rayHitType;

    public Direction direction;

    public PositionVector position;

    public Entity entity;

    public LocationRayHit(PositionVector position, Direction direction, BlockPosition blockPosition) {
        this(RayHitType.BLOCK, position, direction, blockPosition);
    }

    public LocationRayHit(PositionVector position, Direction direction) {
        this(RayHitType.BLOCK, position, direction, BlockPosition.ORIGIN);
    }

    public LocationRayHit(Entity entity) {
        this(entity, new PositionVector(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()));
    }

    public LocationRayHit(RayHitType hitType, PositionVector position, Direction direction, BlockPosition blockPosition) {
        this.rayHitType = hitType;
        this.blockPosition = blockPosition;
        this.direction = direction;
        double hitX = position.getX();
        double hitY = position.getY();
        this.position = new PositionVector(hitX, hitY, position.getZ());
    }

    public LocationRayHit(Entity entity, PositionVector position) {
        this.rayHitType = RayHitType.ENTITY;
        this.entity = entity;
        this.position = position;
    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append("HitResult{type=").append(this.rayHitType).append(", blockpos=").append(this.blockPosition).append(", f=").append(this.direction).append(", pos=").append(this.position).append(", entity=").append(this.entity).append('}').toString();
    }
}
