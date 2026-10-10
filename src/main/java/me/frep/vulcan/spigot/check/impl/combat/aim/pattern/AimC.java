package me.frep.vulcan.spigot.check.impl.combat.aim.pattern;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'C', complexType = "Repeated", description = "Repeated yaw values.")
public class AimC extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaYaw=";

    public AimC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(3))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            float previousDeltaYaw = this.playerData.getRotationTracker().getPreviousDeltaYaw();
            boolean condition = deltaYaw > 1.25F && previousDeltaYaw > 1.25F && deltaYaw == previousDeltaYaw;
            if (condition) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + deltaYaw);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
