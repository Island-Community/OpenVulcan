package me.frep.vulcan.spigot.check.impl.player.badpackets.movement;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Bad Packets", type = 'C', complexType = "Pitch", description = "Impossible pitch.")
public class BadPacketsC extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "pitch=";

    public BadPacketsC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isRotationUpdatePacket(packetEvent)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float pitch = rotationTracker.getPitch();
            if (Math.abs(pitch) > 90.0F) {
                this.fail(VIOLATION_DETAIL_PREFIX + pitch);
            }
        }
    }

    @Override
    public void update() {
    }
}
