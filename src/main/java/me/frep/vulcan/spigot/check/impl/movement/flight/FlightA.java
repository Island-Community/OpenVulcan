package me.frep.vulcan.spigot.check.impl.movement.flight;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Flight", type = 'A', complexType = "Prediction [S]", description = "Invalid Y-Axis movement.")
public class FlightA extends AbstractCheck {

    public FlightA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            if (VulcanSettings.checksMovementFlightAIgnoreGhostBlocks) {
                CollisionState collisionState = this.playerData.getCollisionState();
                if (collisionState.isOnGround()) {
                    CollisionState collisionState2 = this.playerData.getCollisionState();
                    if (collisionState2.isInOpenAir()) {
                        CollisionState collisionState3 = this.playerData.getCollisionState();
                        if (collisionState3.isGroundAligned()) {
                            return;
                        }
                    }
                }
            }
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            if (velocityTracker.isPendingVelocity()) {
                return;
            }
            CollisionState collisionState4 = this.playerData.getCollisionState();
            int ghostActivityTicks = collisionState4.getGhostActivityTicks();
            CollisionState collisionState5 = this.playerData.getCollisionState();
            double deltaY = collisionState5.getDeltaY();
            boolean belowDeltaYDifferenceLimit = Math.abs(deltaY - 0.11760000228882461) <= 0.001;
            if (belowDeltaYDifferenceLimit) {
                if (this.isExempt(new ExemptType[] { ExemptType.FAST })) {
                    return;
                }
            }
            if (deltaY + 0.09800000190734881 <= 0.001) {
                return;
            }
            double previousDeltaY = this.playerData.getCollisionState().getPreviousDeltaY();
            MotionState motionState = this.playerData.getMotionState();
            double scaledPreviousDeltaYDifference = (previousDeltaY - motionState.getGravity()) * 0.98F;
            double deltaYDifference = Math.abs(deltaY - scaledPreviousDeltaYDifference);
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean recentVelocity = velocityTracker2.getTicksSinceVelocity() < 20;
            if (ghostActivityTicks < 30) {
                if (this.isExempt(new ExemptType[] { ExemptType.PISTON })) {
                    return;
                }
            }
            ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
            long movementDelayMillis = connectionTracker.getMovementDelayMillis();
            int index = 6;
            MotionState motionState2 = this.playerData.getMotionState();
            if (motionState2.isPacketJumpBoostEffect()) {
                MotionState motionState3 = this.playerData.getMotionState();
                index += motionState3.getJumpBoostAmplifier();
            }
            boolean condition4;
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            boolean condition5 = velocityTracker3.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            condition4 = ghostActivityTicks > index && deltaYDifference > 1.0E-8 && ghostActivityTicks > 6 && !condition5;
            condition = !((!this.isExempt(new ExemptType[] { ExemptType.SHULKER_BOX, ExemptType.WEB, ExemptType.BOAT, ExemptType.ILLEGAL_BLOCK, ExemptType.SLEEPING, ExemptType.SLOW_FALLING, ExemptType.SHULKER, ExemptType.TRAPDOOR, ExemptType.FISHING_ROD, ExemptType.LILY_PAD, ExemptType.SPECTATOR, ExemptType.HONEY, ExemptType.CHORUS_FRUIT, ExemptType.BED, ExemptType.CHAIN, ExemptType.WORLD_CHANGE, ExemptType.CANCELLED_PLACE, ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.LAGGED_NEAR_GROUND_MODERN, ExemptType.EMPTIED_BUCKET, ExemptType.DEAD, ExemptType.CANCELLED_BREAK, ExemptType.FENCE, ExemptType.LAGGED_NEAR_GROUND, ExemptType.BLOCK_PLACE_FAST, ExemptType.GLASS_PANE, ExemptType.NOT_MOVING })) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptJoined()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptLevitation()) && (!this.playerData.getCollisionState().isExemptSlowFalling()) && (!this.playerData.getCollisionState().isExemptRiptide()) && (!this.playerData.getCollisionState().isExemptVehicle()) && (!this.playerData.getCollisionState().isExemptLenientScaffolding()) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptEnderPearl()) && (!this.playerData.getCollisionState().isExemptComboMode()) && (!this.playerData.getCollisionState().isExemptMythicMob()) && (!this.playerData.getCollisionState().isExemptClimbable()) && (!this.playerData.getCollisionState().isExemptChunk()) && (!this.playerData.getCollisionState().hasSlabAbove()));
            boolean condition6 = condition;
            MotionState motionState6 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState6.getTicksSinceServerPosition();
            VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
            int ticksSinceVelocity = velocityTracker4.getTicksSinceVelocity();
            if (condition4 && !condition6 && !recentVelocity) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaY=" + deltaY + " difference=" + deltaYDifference + " ticks=" + ghostActivityTicks + " delay=" + movementDelayMillis + " tp=" + ticksSinceServerPosition + " vel=" + ticksSinceVelocity);
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
