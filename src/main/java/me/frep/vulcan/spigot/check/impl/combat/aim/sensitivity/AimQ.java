package me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'Q', complexType = "GCD Modulo", description = "GCD bypass flaw detected.")
public class AimQ extends AbstractCheck {

    public AimQ(PlayerData playerData) {
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
                RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
                float yaw = rotationTracker2.getYaw();
                RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
                float pitch = rotationTracker3.getPitch();
                float yawDifference = yaw - yaw % scaledScaledScaledAdjustedScaledSensitivityStep;
                float pitchDifference = pitch - pitch % scaledScaledScaledAdjustedScaledSensitivityStep;
                float yawDifference2 = Math.abs(yaw - yawDifference);
                float pitchDifference2 = Math.abs(pitch - pitchDifference);
                RotationTracker rotationTracker4 = this.playerData.getRotationTracker();
                float deltaYaw = rotationTracker4.getDeltaYaw();
                RotationTracker rotationTracker5 = this.playerData.getRotationTracker();
                float deltaPitch = rotationTracker5.getDeltaPitch();
                float adjustedDeltaYaw = deltaYaw + deltaPitch;
                RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
                double attackDistance = relativeMoveTracker.getAttackDistance();
                boolean condition = (yawDifference2 == 0.0F || pitchDifference2 == 0.0F) && adjustedDeltaYaw > 0.75F;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.SPECTATOR });
                if (condition && attackDistance > 1.0 && !exempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("yawDifference=" + yawDifference2 + " pitchDifference=" + pitchDifference2 + " combined=" + adjustedDeltaYaw);
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
