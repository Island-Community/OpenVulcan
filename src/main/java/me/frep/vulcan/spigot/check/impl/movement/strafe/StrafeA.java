package me.frep.vulcan.spigot.check.impl.movement.strafe;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Strafe", type = 'A', complexType = "Air", description = "Moving incorrectly in the air.")
public class StrafeA extends AbstractCheck {

    private boolean wasSprinting;

    public StrafeA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            boolean wasSprinting2 = motionState.isSprinting();
            int ticksNotSprinting = this.playerData.getMotionState().getTicksNotSprinting();
            CollisionState collisionState = this.playerData.getCollisionState();
            int airTicks = collisionState.getAirTicks();
            double previousDeltaX = this.playerData.getCollisionState().getPreviousDeltaX();
            double previousDeltaZ = this.playerData.getCollisionState().getPreviousDeltaZ();
            double deltaX = this.playerData.getCollisionState().getDeltaX();
            double deltaZ = this.playerData.getCollisionState().getDeltaZ();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaXZ = collisionState2.getDeltaXZ();
            MotionState motionState2 = this.playerData.getMotionState();
            double scaledPreviousDeltaX = previousDeltaX * motionState2.applyAirDrag(0.91F);
            MotionState motionState3 = this.playerData.getMotionState();
            double scaledPreviousDeltaZ = previousDeltaZ * motionState3.applyAirDrag(0.91F);
            double deltaXDifference = deltaX - scaledPreviousDeltaX;
            double deltaZDifference = deltaZ - scaledPreviousDeltaZ;
            double difference = MathUtils.hypot(deltaXDifference, deltaZDifference);
            difference /= this.wasSprinting ? 1.3 : 1.0;
            difference -= !wasSprinting2 && ticksNotSprinting >= 2 ? 0.02 : 0.026;
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            boolean recentVelocity = velocityTracker.getTicksSinceVelocity() < 3;
            CollisionState collisionState3 = this.playerData.getCollisionState();
            boolean recentPiston = collisionState3.getTicksSincePiston() < 30;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.PARTIALLY_STUCK, ExemptType.FIREWORK, ExemptType.DEATH, ExemptType.JOINED, ExemptType.WORLD_CHANGE, ExemptType.FLIGHT, ExemptType.FULLY_STUCK, ExemptType.GLIDING, ExemptType.CREATIVE, ExemptType.SPECTATOR, ExemptType.SOUL_SAND, ExemptType.ELYTRA, ExemptType.VEHICLE, ExemptType.ENTITY_COLLISION, ExemptType.DEPTH_STRIDER, ExemptType.FROZEN, ExemptType.LIQUID, ExemptType.DOLPHINS_GRACE, ExemptType.RIPTIDE, ExemptType.SWIMMING, ExemptType.CLIMBABLE, ExemptType.ENDER_PEARL, ExemptType.FIREBALL, ExemptType.CHORUS_FRUIT, ExemptType.BLOCK_BREAK, ExemptType.CANCELLED_MOVE });
            CollisionState collisionState4 = this.playerData.getCollisionState();
            if (collisionState4.getTicksSinceClimbable() < 100) {
                return;
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean recentExplosion = velocityTracker2.getTicksSinceExplosion() < 10;
            boolean belowStrafeGraceTicksLimit = this.playerData.getMotionState().getStrafeGraceTicks() < 30;
            double amount6 = 0.0075;
            if (this.playerData.getCollisionState().getTicksSinceHorizontalCollision() < 5) {
                amount6 += 0.04;
            }
            boolean condition5 = difference > amount6 && deltaXZ > 0.25 && airTicks > 3;
            if (condition5 && !exempt && !recentPiston && !recentVelocity && !recentExplosion && !belowStrafeGraceTicksLimit) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("difference=" + difference + " deltaXZ=" + deltaXZ);
                }
            } else {
                this.decayBuffer();
            }
            this.wasSprinting = wasSprinting2;
        }
    }

    @Override
    public void update() {
    }
}
