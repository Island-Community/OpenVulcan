package me.frep.vulcan.spigot.check.impl.movement.speed;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Speed", type = 'E', complexType = "Prediction", description = "Invalid friction.")
public class SpeedE extends AbstractCheck {

    public SpeedE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        double movementSpeed;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            double amount = 1.0E-4;
            MotionState motionState = this.playerData.getMotionState();
            double scaledPreviousDeltaXZ = previousDeltaXZ * motionState.applyAirDrag(0.91F);
            MotionState motionState2 = this.playerData.getMotionState();
            double amount3 = motionState2.applyFriction(0.6);
            CollisionState collisionState2 = this.playerData.getCollisionState();
            if (collisionState2.getTicksSinceLiquid() < 30) {
                amount += 0.1;
                CollisionState collisionState3 = this.playerData.getCollisionState();
                if (collisionState3.getTicksSinceDolphinsGrace() < 30) {
                    amount++;
                }
                CollisionState collisionState4 = this.playerData.getCollisionState();
                if (collisionState4.getTicksSinceDolphinsGrace() < 20) {
                    if (this.isExempt(new ExemptType[] { ExemptType.DEPTH_STRIDER })) {
                        amount++;
                    }
                }
                VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
                if (velocityTracker.getTicksSinceExplosion() < 20) {
                    VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
                    amount += velocityTracker2.getCombinedHorizontalExplosion() + 0.05;
                }
                CollisionState collisionState5 = this.playerData.getCollisionState();
                if (collisionState5.isGroundAligned()) {
                    amount += 0.25;
                }
            }
            CollisionState collisionState6 = this.playerData.getCollisionState();
            if (collisionState6.getTicksSinceNearbySlime() < 10) {
                scaledPreviousDeltaXZ += 0.35F;
            }
            CollisionState collisionState7 = this.playerData.getCollisionState();
            if (collisionState7.getTicksSinceEntityCollision() < 20) {
                amount += 0.155;
            }
            CollisionState collisionState8 = this.playerData.getCollisionState();
            if (collisionState8.isOlderOnGround()) {
                scaledPreviousDeltaXZ *= amount3;
            }
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            if (velocityTracker3.isPendingVelocity()) {
                VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
                scaledPreviousDeltaXZ += velocityTracker4.getMaximumHorizontalVelocity() + 0.025;
            }
            VelocityTracker velocityTracker5 = this.playerData.getVelocityTracker();
            if (velocityTracker5.isPendingExplosion()) {
                VelocityTracker velocityTracker6 = this.playerData.getVelocityTracker();
                scaledPreviousDeltaXZ += velocityTracker6.getCombinedHorizontalExplosion() + 0.025;
            }
            CollisionState collisionState9 = this.playerData.getCollisionState();
            if (collisionState9.getTicksSincePiston() < 20) {
                scaledPreviousDeltaXZ += 0.3;
            }
            CollisionState collisionState10 = this.playerData.getCollisionState();
            if (!collisionState10.isPreviousOnGround()) {
                scaledPreviousDeltaXZ += 0.025999999F;
            } else {
                CollisionState collisionState11 = this.playerData.getCollisionState();
                if (!collisionState11.isOnGround()) {
                    CollisionState collisionState12 = this.playerData.getCollisionState();
                    if (collisionState12.getDeltaY() >= 0.0) {
                        scaledPreviousDeltaXZ += 0.2F;
                    } else {
                        MotionState motionState3 = this.playerData.getMotionState();
                        if (!(motionState3.getJumpBoostAmplifier() >= 0)) {
                            scaledPreviousDeltaXZ += 0.2F;
                        }
                    }
                } else {
                    MotionState motionState3 = this.playerData.getMotionState();
                    if (!(motionState3.getJumpBoostAmplifier() >= 0)) {
                        scaledPreviousDeltaXZ += 0.2F;
                    }
                }
                MotionState motionState4 = this.playerData.getMotionState();
                double scaledMovementSpeed = motionState4.getMovementSpeed() * 1.3;
                if (this.playerData.getMotionState().getFrictionModifier() != 1.0) {
                    scaledPreviousDeltaXZ += amount3 <= 0.6 ? scaledMovementSpeed : scaledMovementSpeed * 0.21600002 / Math.pow(amount3, 3.0);
                } else {
                    scaledPreviousDeltaXZ += scaledMovementSpeed * 0.16277136 / Math.pow(0.546, 3.0);
                }
            }
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceGliding() < 20) {
                CollisionState collisionState13 = this.playerData.getCollisionState();
                scaledPreviousDeltaXZ += collisionState13.getLastGlidingDeltaXZ();
            }
            CollisionState collisionState14 = this.playerData.getCollisionState();
            if (collisionState14.getTicksSinceIce() < 20) {
                if (this.playerData.getCollisionState().getTicksSinceTrapdoor() < 20) {
                    amount += 0.41;
                }
            }
            CollisionState collisionState15 = this.playerData.getCollisionState();
            if (collisionState15.getTicksSinceIce() < 15) {
                amount += 0.31;
            }
            CollisionState collisionState16 = this.playerData.getCollisionState();
            if (collisionState16.getTicksSinceSlime() < 15) {
                amount += 0.21;
            }
            CollisionState collisionState17 = this.playerData.getCollisionState();
            if (collisionState17.getTicksSinceFarmland() < 15) {
                amount += 0.31;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.isSwiftSneak()) {
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.getTicksSinceBlockPlace() < 30) {
                    amount += 0.15;
                }
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FARMLAND })) {
                amount += 0.05;
            }
            CollisionState collisionState18 = this.playerData.getCollisionState();
            if (collisionState18.getTicksSinceIce() < 15) {
                CollisionState collisionState19 = this.playerData.getCollisionState();
                if (collisionState19.getTicksSinceVerticalCollision() < 15) {
                    amount += 0.35;
                }
            }
            CollisionState collisionState20 = this.playerData.getCollisionState();
            if (collisionState20.getTicksSinceFishingRod() < 30) {
                amount += 0.3;
            }
            CollisionState collisionState21 = this.playerData.getCollisionState();
            if (collisionState21.getTicksSinceSwimming() < 20) {
                MotionState motionState8 = this.playerData.getMotionState();
                if (motionState8.getTicksSinceBlockPlace() < 20) {
                    amount += 0.15;
                }
            }
            if (this.playerData.getVelocityTracker().getTicksSinceLargeVelocity() < 25) {
                if (this.playerData.getCollisionState().getTicksSinceIceContact() < 25) {
                    amount += 0.525;
                }
            }
            MotionState motionState9 = this.playerData.getMotionState();
            if (motionState9.getTicksSinceSoulPlace() < 30) {
                MotionState motionState10 = this.playerData.getMotionState();
                if (motionState10.isSwiftSneak()) {
                    if (this.playerData.getCollisionState().getTicksSinceSoulSpeed() < 30) {
                        amount += 0.15;
                    }
                }
            }
            double deltaXZDifference;
            double amount6;
            deltaXZDifference = deltaXZ - scaledPreviousDeltaXZ;
            MotionState motionState11 = this.playerData.getMotionState();
            movementSpeed = motionState11.getMovementSpeed();
            CollisionState collisionState22 = this.playerData.getCollisionState();
            if (collisionState22.isOnGround()) {
                CollisionState collisionState23 = this.playerData.getCollisionState();
                if (collisionState23.isPreviousOnGround()) {
                    amount6 = 2.0;
                } else {
                    amount6 = 1.125;
                }
            } else {
                amount6 = 1.125;
            }
            boolean condition2;
            double amount7 = Math.max(0.2625, movementSpeed * amount6);
            condition2 = deltaXZDifference > amount && deltaXZ > amount7;
            condition = (this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.TELEPORT, ExemptType.SLEEPING, ExemptType.GLIDING, ExemptType.RIPTIDE, ExemptType.WEB, ExemptType.CREATIVE, ExemptType.ENDER_PEARL, ExemptType.VEHICLE, ExemptType.WORLD_CHANGE, ExemptType.CHUNK, ExemptType.TELEPORT, ExemptType.HONEY, ExemptType.CHORUS_FRUIT })) || (!((this.playerData.getCollisionState()).getTicksSincePiston() >= 20));
            boolean condition3 = condition;
            if (condition2 && !condition3) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    StringBuilder stringBuilder = new StringBuilder().append("dXZ=").append(deltaXZ).append(" diff=").append(deltaXZDifference).append(" min=").append(amount).append(" tp=");
                    MotionState motionState12 = this.playerData.getMotionState();
                    StringBuilder stringBuilder2 = stringBuilder.append(motionState12.getTicksSinceServerPosition()).append(" g=");
                    MotionState motionState13 = this.playerData.getMotionState();
                    StringBuilder stringBuilder3 = stringBuilder2.append(motionState13.isPacketGliding()).append(" v=");
                    VelocityTracker velocityTracker7 = this.playerData.getVelocityTracker();
                    this.fail(stringBuilder3.append(velocityTracker7.getTicksSinceVelocity()).toString());
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
