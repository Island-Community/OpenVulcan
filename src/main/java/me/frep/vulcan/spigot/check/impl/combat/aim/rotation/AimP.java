package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'P', complexType = "Yaw Acceleration", experimental = false, description = "Large yaw acceleration.")
public class AimP extends AbstractCheck {

    public AimP(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(3))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float pitch = rotationTracker.getPitch();
            float yawAcceleration = this.playerData.getRotationTracker().getYawAcceleration();
            float pitchAcceleration = this.playerData.getRotationTracker().getPitchAcceleration();
            RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
            double targetDistance = relativeMoveTracker.getTargetDistance();
            boolean condition = yawAcceleration > 20.0F && pitchAcceleration < 0.05 && Math.abs(pitch) < 60.0F && targetDistance > 1.0;
            if (condition) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("yawAccel=" + yawAcceleration + " pitchAccel=" + pitchAcceleration);
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
