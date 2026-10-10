package me.frep.vulcan.spigot.check.impl.combat.aim.pattern;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Aim", type = 'K', complexType = "Linear", experimental = false, description = "Not constant rotations.")
public class AimK extends AbstractCheck {

    public AimK(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker2.getDeltaPitch();
            float previousDeltaYaw = this.playerData.getRotationTracker().getPreviousDeltaYaw();
            RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
            float previousDeltaPitch = rotationTracker3.getPreviousDeltaPitch();
            long scaledDeltaYaw = (long) (deltaYaw * MathUtils.ROTATION_GCD_SCALE);
            long scaledPreviousDeltaYaw = (long) (previousDeltaYaw * MathUtils.ROTATION_GCD_SCALE);
            double amount = MathUtils.approximateGcd(scaledDeltaYaw, scaledPreviousDeltaYaw);
            long scaledDeltaPitch = (long) (deltaPitch * MathUtils.ROTATION_GCD_SCALE);
            scaledPreviousDeltaYaw = (long) (previousDeltaPitch * MathUtils.ROTATION_GCD_SCALE);
            double amount2 = MathUtils.approximateGcd(scaledDeltaPitch, scaledPreviousDeltaYaw);
            double scaledRotationGcdScale = amount / MathUtils.ROTATION_GCD_SCALE;
            double scaledRotationGcdScale2 = amount2 / MathUtils.ROTATION_GCD_SCALE;
            double scaledDeltaYaw2 = deltaYaw / scaledRotationGcdScale;
            double scaledDeltaPitch2 = deltaPitch / scaledRotationGcdScale2;
            double scaledPreviousDeltaYaw2 = previousDeltaYaw / scaledRotationGcdScale;
            double scaledPreviousDeltaPitch = previousDeltaPitch / scaledRotationGcdScale2;
            if (deltaYaw > 0.1F && deltaPitch > 0.1F && deltaYaw < 20.0F && deltaPitch < 20.0F) {
                double scaledDeltaYaw2Remainder = scaledDeltaYaw2 % scaledPreviousDeltaYaw2;
                double scaledDeltaPitch2Remainder = scaledDeltaPitch2 % scaledPreviousDeltaPitch;
                double scaledDeltaYaw2RemainderDifference = Math.abs(Math.floor(scaledDeltaYaw2Remainder) - scaledDeltaYaw2Remainder);
                double scaledDeltaPitch2RemainderDifference = Math.abs(Math.floor(scaledDeltaPitch2Remainder) - scaledDeltaPitch2Remainder);
                boolean condition = scaledDeltaYaw2Remainder > 60.0 && scaledDeltaYaw2RemainderDifference > 0.1;
                boolean condition2 = scaledDeltaPitch2Remainder > 60.0 && scaledDeltaPitch2RemainderDifference > 0.1;
                RotationTracker rotationTracker4 = this.playerData.getRotationTracker();
                double sensitivity = rotationTracker4.getSensitivity();
                boolean condition3 = sensitivity < 100.0 && sensitivity > -1.0;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CINEMATIC });
                if (condition && condition2 && !exempt && !condition3) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
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
