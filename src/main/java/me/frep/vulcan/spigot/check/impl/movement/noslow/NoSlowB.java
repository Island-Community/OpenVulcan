package me.frep.vulcan.spigot.check.impl.movement.noslow;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "No Slow", type = 'B', complexType = "Soul Sand", description = "Moving too quickly on Soul Sand.")
public class NoSlowB extends AbstractCheck {

    public NoSlowB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if (this.isPositionUpdatePacket(packetEvent)) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            float maxSpeed = CheckUtils.getMaxSpeed(this.playerData, 0.2F);
            MotionState motionState = this.playerData.getMotionState();
            double adjustedMaxSpeed = maxSpeed + motionState.getSpeedAmplifier() * 0.0625F;
            boolean exempt;
            boolean condition2;
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean onSoulSand = collisionState2.isOnSoulSand();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            boolean groundAligned = collisionState3.isGroundAligned();
            exempt = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VELOCITY, ExemptType.TELEPORT, ExemptType.ENDER_PEARL, ExemptType.CHORUS_FRUIT, ExemptType.SOUL_SPEED, ExemptType.ELYTRA, ExemptType.RIPTIDE, ExemptType.HIGH_SPEED, ExemptType.DEPTH_STRIDER });
            condition2 = onSoulSand && deltaXZ > adjustedMaxSpeed && groundAligned;
            condition = (this.playerData.getCollisionState().getWalkSpeed() > 0.11F) || ((this.playerData.getMotionState()).getMovementSpeed() > 0.11F);
            boolean condition3 = condition;
            if (condition2 && !exempt && !condition3) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaXZ=" + deltaXZ + " max=" + adjustedMaxSpeed);
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
