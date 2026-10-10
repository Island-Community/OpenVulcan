package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'G', complexType = "Speed", experimental = false, description = "Bridging too quickly.")
public class ScaffoldG extends AbstractCheck {

    private long lastPlacementAtMillis;

    public ScaffoldG(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled() || BlockUtils.isSlab(blockPlaceEvent.getBlock().getType())) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.isSwiftSneak()) {
                return;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SLAB })) {
                return;
            }
            boolean validBlockPlacement = CheckUtils.isValidBlockPlacement(blockPlaceEvent);
            if (validBlockPlacement) {
                long currentTimeMillisDifference = System.currentTimeMillis() - this.lastPlacementAtMillis;
                RotationTracker rotationTracker = this.playerData.getRotationTracker();
                float movementLookAngle = rotationTracker.getMovementLookAngle();
                double deltaXZChange = this.playerData.getCollisionState().getDeltaXZChange();
                boolean scaffolding = BlockUtils.isScaffolding(blockPlaceEvent.getBlock());
                boolean condition = deltaXZChange < 0.001 && deltaXZChange > 0.0 && currentTimeMillisDifference < 300L && movementLookAngle < 2.0F;
                if (condition && !scaffolding) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("angle=" + movementLookAngle + " delay=" + currentTimeMillisDifference + " acceleration=" + deltaXZChange);
                    }
                } else {
                    this.decayBuffer();
                }
                this.lastPlacementAtMillis = System.currentTimeMillis();
            }
        }
    }
}
