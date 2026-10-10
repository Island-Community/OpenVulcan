package me.frep.vulcan.spigot.check.impl.player.baritone;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Baritone", type = 'A', complexType = "Rotations", description = "Checks for Baritone like rotations.")
public class BaritoneA extends AbstractCheck {

    private int rotationsSinceDigCancellation;

    public BaritoneA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isRotationUpdatePacket(packetEvent) && ++this.rotationsSinceDigCancellation < 10) {
            RotationTracker rotation = this.playerData.getRotationTracker();
            float deltaYaw = rotation.getDeltaYaw();
            float deltaPitch = rotation.getDeltaPitch();
            boolean cinematic = rotation.isCinematic();
            if (deltaYaw < 0.005 && deltaPitch < 0.005 && deltaYaw > 0.0 && deltaPitch > 0.0 && !cinematic) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
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
