package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'Y', complexType = "Rotation", description = "Generic rotation analysis heuristic.")
public class AimY extends AbstractCheck {

    public AimY(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3) && (!this.isTeleporting())) {
            float difference;
            double attackDistance;
            boolean condition;
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float previousYawSmoothing = rotationTracker.getPreviousYawSmoothing();
            float yawSmoothing = this.playerData.getRotationTracker().getYawSmoothing();
            difference = Math.abs(yawSmoothing - previousYawSmoothing);
            RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
            attackDistance = relativeMoveTracker.getAttackDistance();
            condition = (this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.DEATH, ExemptType.WORLD_CHANGE })) || (!((this.playerData.getMotionState()).getTicksSinceServerPosition() >= 20));
            boolean condition2 = condition;
            if (condition2) {
                return;
            }
            if (attackDistance > 0.6 && difference > 20.0F && attackDistance < 10.0) {
                this.fail("diff=" + difference + " dist=" + attackDistance);
            }
        }
    }

    @Override
    public void update() {
    }
}
