package me.frep.vulcan.spigot.check.impl.player.scaffold;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'E', complexType = "Rotations [2]", experimental = false, description = "Invalid rotations.")
public class ScaffoldE extends AbstractCheck {

    private long lastPlacementAtMillis;

    private long placementIntervalMillis;

    private boolean lastPlacementValid;

    private int movementsSincePlacement;

    public ScaffoldE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent) && ++this.movementsSincePlacement < 3 && this.lastPlacementValid && this.placementIntervalMillis < 400L) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker.getDeltaPitch();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker2.getDeltaYaw();
            if (deltaPitch > 8.0F && deltaYaw > 100.0F) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
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
            this.movementsSincePlacement = 0;
            this.lastPlacementAtMillis = System.currentTimeMillis();
        }
    }
}
