package me.frep.vulcan.spigot.check.impl.player.scaffold;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'I', complexType = "Acceleration", description = "Invalid acceleration.")
public class ScaffoldI extends AbstractCheck {

    public ScaffoldI(PlayerData playerData) {
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
            if (this.playerData.getMotionState().isLastPlacementValid()) {
                double deltaXZChange = this.playerData.getCollisionState().getDeltaXZChange();
                RotationTracker rotationTracker = this.playerData.getRotationTracker();
                float deltaPitch = rotationTracker.getDeltaPitch();
                RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
                float deltaYaw = rotationTracker2.getDeltaYaw();
                boolean condition = deltaXZChange < 0.01 && deltaYaw > 150.0F && deltaPitch > 10.0F;
                if (condition) {
                    this.fail("acceleration=" + deltaXZChange + " deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
                }
            }
        }
    }
}
