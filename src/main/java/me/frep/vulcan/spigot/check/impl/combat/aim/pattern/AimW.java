package me.frep.vulcan.spigot.check.impl.combat.aim.pattern;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'W', complexType = "Analysis", description = "Generic rotation analysis heuristic.")
public class AimW extends AbstractCheck {

    public AimW(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker2.getDeltaPitch();
            RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
            int sensitivity = rotationTracker3.getSensitivity();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CINEMATIC });
            boolean condition = sensitivity < -10 && deltaYaw > 1.25F && deltaPitch > 1.25F;
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("sens=" + sensitivity + " deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
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
