package me.frep.vulcan.spigot.check.impl.player.baritone;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Baritone", type = 'B', complexType = "Large Rotation", description = "Checks for Baritone like rotations.")
public class BaritoneB extends AbstractCheck {

    private int rotationsSinceDigCancellation = 100;

    public BaritoneB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isRotationUpdatePacket(packetEvent) && ++this.rotationsSinceDigCancellation < 5) {
            RotationTracker rotation = this.playerData.getRotationTracker();
            float deltaYaw = rotation.getDeltaYaw();
            float deltaPitch = rotation.getDeltaPitch();
            float pitch = rotation.getPitch();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.PISTON });
            if (deltaYaw > 95.0F && deltaPitch < 0.15 && Math.abs(pitch) < 70.0F && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaPitch=" + deltaPitch + " deltaYaw=" + deltaYaw);
                }
            } else {
                this.decayBuffer();
            }
        } else if (this.isDiggingPacket(packetEvent)) {
            WrapperPlayClientPlayerDigging lastDiggingPacket = this.playerData.getLastDiggingPacket();
            if (lastDiggingPacket.getAction() == DiggingAction.CANCELLED_DIGGING) {
                this.rotationsSinceDigCancellation = 0;
            }
        }
    }

    @Override
    public void update() {
    }
}
