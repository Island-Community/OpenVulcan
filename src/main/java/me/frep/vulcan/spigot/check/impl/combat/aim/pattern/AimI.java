package me.frep.vulcan.spigot.check.impl.combat.aim.pattern;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Aim", type = 'I', complexType = "Constant", experimental = false, description = "Not constant rotations.")
public class AimI extends AbstractCheck {

    public AimI(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker.getDeltaPitch();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float previousDeltaPitch = rotationTracker2.getPreviousDeltaPitch();
            RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker3.getDeltaYaw();
            long scaledRotationGcdScale = (long) (MathUtils.ROTATION_GCD_SCALE * deltaPitch);
            long scaledRotationGcdScale2 = (long) (MathUtils.ROTATION_GCD_SCALE * previousDeltaPitch);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CINEMATIC });
            long longValue3 = MathUtils.approximateGcd(scaledRotationGcdScale, scaledRotationGcdScale2);
            RotationTracker rotationTracker4 = this.playerData.getRotationTracker();
            boolean hasKnownSensitivity2 = rotationTracker4.hasKnownSensitivity();
            boolean condition = deltaYaw > 0.25F && deltaPitch > 0.25F && deltaPitch < 20.0F && deltaYaw < 20.0F;
            boolean condition2 = !exempt && longValue3 < 131072L;
            if (condition2 && condition && !hasKnownSensitivity2) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("rotation=" + longValue3 / 1000L + " deltaPitch=" + deltaPitch + " deltaYaw=" + deltaYaw);
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
