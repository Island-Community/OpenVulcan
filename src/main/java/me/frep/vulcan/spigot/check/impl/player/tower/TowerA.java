package me.frep.vulcan.spigot.check.impl.player.tower;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Tower", type = 'A', complexType = "Limit", experimental = false, description = "Towering too quickly.")
public class TowerA extends AbstractCheck {

    private long lastPlacementAtMillis;

    public TowerA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent) {
            if (this.playerData.getPlayer() == null) {
                return;
            }
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event2;
            if (blockPlaceEvent.isCancelled() || !blockPlaceEvent.getBlock().getType().isSolid()) {
                return;
            }
            long delay = System.currentTimeMillis() - this.lastPlacementAtMillis;
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            boolean condition = this.playerData.getPlayer().getLocation().getBlockX() == blockPlaceEvent.getBlock().getLocation().getBlockX();
            boolean condition2 = this.playerData.getPlayer().getLocation().getBlockZ() == blockPlaceEvent.getBlock().getLocation().getBlockZ();
            BlockFace blockFace = blockPlaceEvent.getBlockAgainst().getFace(blockPlaceEvent.getBlock());
            boolean condition3 = BlockUtils.isScaffolding(blockPlaceEvent.getBlock()) || BlockUtils.isTripwire(blockPlaceEvent.getBlock()) || BlockUtils.isTurtleEgg(blockPlaceEvent.getBlock()) || BlockUtils.isClimbable(blockPlaceEvent.getBlock());
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.FLIGHT, ExemptType.JUMP_BOOST, ExemptType.CANCELLED_PLACE, ExemptType.LIQUID, ExemptType.TURTLE_EGG, ExemptType.CLIMBABLE, ExemptType.EXPLOSION, ExemptType.ELYTRA, ExemptType.PACKET_EXPLOSION, ExemptType.SLOW_FALLING });
            boolean condition4 = blockFace == BlockFace.UP && condition && condition2 && delay < 250L && deltaY > 0.0 && deltaXZ < 0.1;
            if (condition4 && !exempt && !condition3) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("delay=" + delay + " deltaY=" + deltaY);
                }
            } else {
                this.decayBuffer();
            }
            this.lastPlacementAtMillis = System.currentTimeMillis();
        }
    }
}
