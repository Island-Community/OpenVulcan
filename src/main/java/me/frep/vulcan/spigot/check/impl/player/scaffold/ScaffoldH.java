package me.frep.vulcan.spigot.check.impl.player.scaffold;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'H', complexType = "Rotations [3]", experimental = false, description = "Invalid rotations.")
public class ScaffoldH extends AbstractCheck {

    private long lastPlacementAtMillis;

    private long placementIntervalMillis;

    private boolean lastPlacementValid;

    private int rotationsSincePlacement;

    public ScaffoldH(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isRotationUpdatePacket(packetEvent) && ++this.rotationsSincePlacement < 5 && this.lastPlacementValid && this.placementIntervalMillis < 400L) {
            double sensitivityEstimate;
            float deltaPitch;
            boolean condition;
            double deltaXZ;
            boolean condition2;
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            sensitivityEstimate = rotationTracker.getSensitivityEstimate();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            deltaPitch = rotationTracker2.getDeltaPitch();
            condition = sensitivityEstimate < -2.0 || sensitivityEstimate > 200.0 && deltaPitch > 0.55F;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaXZ = collisionState.getDeltaXZ();
            condition2 = (this.isExempt(new ExemptType[] { ExemptType.CINEMATIC })) || ((this.playerData.getMotionState()).isSwiftSneak());
            boolean condition3 = condition2;
            if (condition && !condition3 && deltaXZ > 0.25) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("final=" + sensitivityEstimate + " dp=" + deltaPitch);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled()) {
                return;
            }
            this.lastPlacementValid = CheckUtils.isValidBlockPlacement(blockPlaceEvent);
            this.placementIntervalMillis = System.currentTimeMillis() - this.lastPlacementAtMillis;
            this.rotationsSincePlacement = 0;
            this.lastPlacementAtMillis = System.currentTimeMillis();
        }
    }
}
