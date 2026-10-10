package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'M', complexType = "Expand", description = "Invalid block face.")
public class ScaffoldM extends AbstractCheck {

    public ScaffoldM(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled()) {
                return;
            }
            if (BlockUtils.isScaffolding(blockPlaceEvent.getBlock().getType())) {
                return;
            }
            double y = blockPlaceEvent.getBlockPlaced().getY();
            CollisionState collisionState = this.playerData.getCollisionState();
            boolean belowYLimit = y < collisionState.getY();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double xDifference = Math.abs(collisionState2.getX() - blockPlaceEvent.getBlockPlaced().getX());
            CollisionState collisionState3 = this.playerData.getCollisionState();
            double zDifference = Math.abs(collisionState3.getZ() - blockPlaceEvent.getBlockPlaced().getZ());
            double distance = MathUtils.hypot(xDifference, zDifference);
            BlockFace blockFace = blockPlaceEvent.getBlockAgainst().getFace(blockPlaceEvent.getBlock());
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float yaw = rotationTracker.getYaw();
            BlockFace blockFace2 = this.getFacingFromYaw(yaw);
            boolean condition2 = belowYLimit && distance > 1.0 && distance < 6.0 && blockFace == blockFace2;
            if (condition2) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("[1] face=" + blockFace + " direction=" + blockFace2 + " distance=" + distance);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    private BlockFace getFacingFromYaw(float amount) {
        float amount2 = (amount - 90.0F) % 360.0F;
        if (amount2 < 0.0F) {
            amount2 = (float) (amount2 + 360.0);
        }
        return this.getFacingFromNormalizedYaw(amount2);
    }

    private BlockFace getFacingFromNormalizedYaw(float amount) {
        if (0.0F <= amount && amount < 45.0F) {
            return BlockFace.WEST;
        }
        if (45.0F <= amount && amount < 135.0F) {
            return BlockFace.NORTH;
        }
        if (135.0F <= amount && amount < 225.0F) {
            return BlockFace.EAST;
        }
        if (225.0F <= amount && amount < 315.0F) {
            return BlockFace.SOUTH;
        }
        return 315.0F <= amount && amount < 360.0 ? BlockFace.WEST : BlockFace.SELF;
    }
}
