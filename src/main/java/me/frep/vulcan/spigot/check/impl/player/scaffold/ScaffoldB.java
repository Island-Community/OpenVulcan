package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'B', complexType = "Interact", experimental = false, description = "Invalid interact.")
public class ScaffoldB extends AbstractCheck {

    private long lastPlacementAtMillis;

    public ScaffoldB(PlayerData playerData) {
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
            if (this.isExempt(new ExemptType[] { ExemptType.CANCELLED_PLACE })) {
                return;
            }
            long delay = System.currentTimeMillis() - this.lastPlacementAtMillis;
            BlockFace blockFace = blockPlaceEvent.getBlockAgainst().getFace(blockPlaceEvent.getBlock());
            boolean condition = BlockUtils.isTurtleEgg(blockPlaceEvent.getBlock()) || BlockUtils.isSlab(blockPlaceEvent.getBlock()) || BlockUtils.isTnt(blockPlaceEvent.getBlock().getType()) || BlockUtils.isConcretePowder(blockPlaceEvent.getBlock().getType());
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LIQUID, ExemptType.CREATIVE, ExemptType.FLIGHT, ExemptType.TURTLE_EGG, ExemptType.SLAB });
            boolean condition2 = this.playerData.getMotionState().isLastPlacementValid() && blockFace == BlockFace.SELF && delay < 300L;
            if (condition2 && !exempt && !condition) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("delay=" + delay + " face=" + blockFace.toString().toLowerCase());
                }
            } else {
                this.decayBuffer();
            }
            this.lastPlacementAtMillis = System.currentTimeMillis();
        }
    }
}
