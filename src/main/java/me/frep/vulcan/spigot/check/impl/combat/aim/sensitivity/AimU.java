package me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'U', complexType = "GCD Flaw", description = "GCD bypass.")
public class AimU extends AbstractCheck {

    public AimU(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            boolean hasValidSensitivity2 = rotationTracker.hasValidSensitivity();
            if (hasValidSensitivity2) {
                double sensitivityStep = this.playerData.getRotationTracker().getSensitivityStep();
                float adjustedScaledSensitivityStep = (float) sensitivityStep * 0.6F + 0.2F;
                float scaledScaledScaledAdjustedScaledSensitivityStep = adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * 1.2F;
                RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
                float yaw = rotationTracker2.getYaw();
                RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
                float pitch = rotationTracker3.getPitch();
                float yawDifference = yaw - yaw % scaledScaledScaledAdjustedScaledSensitivityStep;
                float pitchDifference = pitch - pitch % scaledScaledScaledAdjustedScaledSensitivityStep;
                float yawDiff = Math.abs(yaw - yawDifference);
                float pitchDifference2 = Math.abs(pitch - pitchDifference);
                if (yawDiff == 0.01F || pitchDifference2 == 0.01F) {
                    this.fail("[I] yawDiff=" + yawDiff + " pitchDiff=" + pitchDifference2);
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
