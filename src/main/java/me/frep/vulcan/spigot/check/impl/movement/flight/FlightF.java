package me.frep.vulcan.spigot.check.impl.movement.flight;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.data.RotationSample;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Flight", type = 'F', complexType = "Prediction", description = "Invalid Y-Axis movement.")
public class FlightF extends AbstractCheck {

    public FlightF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getJumpBoostAmplifier() > 50) {
                return;
            }
            MotionState motionState3 = this.playerData.getMotionState();
            if (motionState3.isPacketGliding()) {
                return;
            }
            MotionState motionState4 = this.playerData.getMotionState();
            if (motionState4.getTicksSinceServerPosition() == 1) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            RotationSample rotationSample = collisionState.getRotationSample(0);
            CollisionState collisionState2 = this.playerData.getCollisionState();
            RotationSample rotationSample2 = collisionState2.getRotationSample(1);
            boolean onGround = rotationSample.isOnGround();
            boolean onGround2 = rotationSample2.isOnGround();
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            boolean pendingVelocity = velocityTracker.isPendingVelocity();
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean pendingExplosion = velocityTracker2.isPendingExplosion();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            int airTicks = collisionState3.getAirTicks();
            double deltaY = rotationSample.getDeltaY();
            double deltaY2 = rotationSample2.getDeltaY();
            double predictedDeltaY = this.getPredictedDeltaY();
            double deltaYDifference = Math.abs(deltaY - predictedDeltaY);
            double amount2 = 1.0E-8;
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceGravityChange() < 20) {
                return;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.getTicksSinceServerPosition() == 1) {
                amount2 += 0.01;
            }
            if (airTicks == 7) {
                amount2 += 0.03;
            }
            if (Math.abs(deltaY - 0.40444491418477924) <= 0.025) {
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.getTicksSinceBlockPlace() < 20) {
                    amount2 += 0.75;
                }
            }
            if (this.playerData.getCollisionState().getTicksWithoutGliding() < 10) {
                amount2 += 0.6;
            }
            MotionState motionState8 = this.playerData.getMotionState();
            if (motionState8.getTicksSinceGliding() < 30) {
                amount2 += 0.1;
            }
            if (this.playerData.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
                if (this.playerData.getCollisionState().isPreviousPositionChanged()) {
                    if (!(this.playerData.getCollisionState().isOlderPositionChanged())) {
                        amount2 += 0.03;
                    }
                } else {
                    amount2 += 0.03;
                }
                MotionState motionState9 = this.playerData.getMotionState();
                if (motionState9.getTicksSinceBlockPlace() < 20) {
                    RotationTracker rotationTracker = this.playerData.getRotationTracker();
                    if (rotationTracker.getPitch() < 45.0F) {
                        amount2 += 0.02;
                    }
                }
                if (airTicks == 6) {
                    CollisionState collisionState4 = this.playerData.getCollisionState();
                    if (collisionState4.getDeltaXZ() < 0.1) {
                        amount2 += 0.1;
                    }
                }
                if (airTicks == 7) {
                    CollisionState collisionState5 = this.playerData.getCollisionState();
                    if (collisionState5.getDeltaXZ() < 0.1) {
                        amount2 += 0.2;
                    }
                }
                if (airTicks == 7) {
                    MotionState motionState10 = this.playerData.getMotionState();
                    if (Math.abs(deltaY - (0.0 - motionState10.getGravity()) * 0.98F) < 5.0E-4) {
                        amount2 += 0.2;
                    }
                }
            }
            boolean condition2;
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            boolean condition3 = velocityTracker3.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            condition2 = !rotationSample.isTeleportInvalidated() && !onGround && !onGround2 && deltaYDifference > amount2 && !pendingVelocity && !pendingExplosion && !condition3;
            condition = (this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.LIQUID, ExemptType.WALL, ExemptType.CONDUIT, ExemptType.FARMLAND, ExemptType.SLOW_FALLING, ExemptType.LEVITATION, ExemptType.RIPTIDE, ExemptType.WEB, ExemptType.WATERLOGGED, ExemptType.SHULKER_BOX, ExemptType.SPECTATOR, ExemptType.SLEEPING, ExemptType.FISHING_ROD, ExemptType.SLIME, ExemptType.AROUND_SLIME, ExemptType.CHUNK, ExemptType.PLACED_SLIME, ExemptType.POWDER_SNOW, ExemptType.SEAGRASS, ExemptType.SCAFFOLDING, ExemptType.CLIMBABLE, ExemptType.SWIMMING, ExemptType.SHULKER, ExemptType.SIGN, ExemptType.JOINED_CHUNK_LOAD, ExemptType.EMPTIED_BUCKET, ExemptType.SWEET_BERRIES, ExemptType.BOAT, ExemptType.HONEY, ExemptType.SEA_PICKLE, ExemptType.ENDER_PEARL, ExemptType.TELEPORT, ExemptType.BUBBLE_COLUMN, ExemptType.VEHICLE, ExemptType.CHEST, ExemptType.PLACED_WEB, ExemptType.KELP, ExemptType.GLIDING, ExemptType.GLITCHED_BLOCKS_ABOVE })) || ((this.playerData.getCollisionState().isNearDriedGhast()) || (!((this.playerData.getCollisionState()).getTicksSincePiston() >= 20) || (!((this.playerData.getCollisionState()).getTicksSinceVerticalCollision() >= 20) || (this.playerData.getCollisionState().hasSlabAbove()))));
            boolean condition4 = condition;
            if (condition2 && !condition4) {
                if (!(this.getPredictedDeltaY() > this.maximumBuffer)) {
                    if (!(deltaYDifference > 1.0)) {
                        return;
                    }
                    if (this.playerData.getCollisionState().getTicksSinceHorizontalCollision() <= 20) {
                        return;
                    }
                }
                StringBuilder stringBuilder = new StringBuilder().append("dY=").append(deltaY).append(" ldY=").append(deltaY2).append(" pre=").append(predictedDeltaY).append(" diff=").append(deltaYDifference).append(" tol=").append(amount2).append(" tick=").append(airTicks).append(" tp=");
                MotionState motionState11 = this.playerData.getMotionState();
                StringBuilder stringBuilder2 = stringBuilder.append(motionState11.getTicksSinceServerPosition()).append(" t=");
                MotionState motionState12 = this.playerData.getMotionState();
                StringBuilder stringBuilder3 = stringBuilder2.append(motionState12.getTicksSinceGliding()).append(" v=");
                VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
                this.fail(stringBuilder3.append(velocityTracker4.getTicksSinceVelocity()).toString());
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }

    private double getPredictedDeltaY() {
        CollisionState collisionState = this.playerData.getCollisionState();
        RotationSample rotationSample = collisionState.getRotationSample(1);
        if ((rotationSample.isTeleportInvalidated())) {
            return 0.0;
        }
        MotionState motionState = this.playerData.getMotionState();
        double airDrag = motionState.applyAirDrag(0.98F);
        double lastDeltaY = rotationSample.getDeltaY();
        MotionState motionState2 = this.playerData.getMotionState();
        double predicted = (lastDeltaY - motionState2.getGravity()) * airDrag;
        if (Math.abs(predicted) < (this.playerData.isOlderThan1_9() ? 0.005 : 0.003)) {
            predicted = 0.0;
        }
        return predicted;
    }
}
