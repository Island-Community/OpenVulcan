package me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'S', complexType = "Divisor Y", experimental = false, description = "Subtle aim assist modifications.")
public class AimS extends AbstractCheck {

    public AimS(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            boolean hasValidSensitivity2 = rotationTracker.hasValidSensitivity();
            if (hasValidSensitivity2) {
                double sensitivityStep = this.playerData.getRotationTracker().getSensitivityStep();
                if (sensitivityStep < 0.01) {
                    return;
                }
                float adjustedScaledSensitivityStep = (float) sensitivityStep * 0.6F + 0.2F;
                float scaledScaledScaledAdjustedScaledSensitivityStep = adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * adjustedScaledSensitivityStep * 1.2F;
                if (scaledScaledScaledAdjustedScaledSensitivityStep < 1.0E-4) {
                    return;
                }
                RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
                float deltaYaw = rotationTracker2.getDeltaYaw();
                RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
                float deltaPitch = rotationTracker3.getDeltaPitch();
                double scaledDeltaYaw = deltaYaw / scaledScaledScaledAdjustedScaledSensitivityStep;
                double scaledDeltaPitch = deltaPitch / scaledScaledScaledAdjustedScaledSensitivityStep;
                double divisorX = Math.abs(Math.round(scaledDeltaYaw) - scaledDeltaYaw);
                double divisorY = Math.abs(Math.round(scaledDeltaPitch) - scaledDeltaPitch);
                if (divisorY > 0.03 && divisorX < 1.0E-4) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("divisorX=" + divisorX + " divisorY=" + divisorY);
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
