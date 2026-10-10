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

@CheckInfo(name = "Flight", type = 'E', complexType = "Hover", description = "Invalid Y-Axis movement.")
public class FlightE extends AbstractCheck {

    public FlightE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            if (VulcanSettings.checksMovementFlightEIgnoreGhostBlocks) {
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
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getGravity() != 0.08) {
                return;
            }
            double deltaY;
            double deltaYDifference;
            int ghostActivityTicks;
            long movementDelayMillis;
            boolean condition2;
            CollisionState collisionState4 = this.playerData.getCollisionState();
            deltaY = collisionState4.getDeltaY();
            double previousDeltaY = this.playerData.getCollisionState().getPreviousDeltaY();
            deltaYDifference = Math.abs(deltaY - previousDeltaY);
            CollisionState collisionState5 = this.playerData.getCollisionState();
            boolean inOpenAir = collisionState5.isInOpenAir();
            CollisionState collisionState6 = this.playerData.getCollisionState();
            ghostActivityTicks = collisionState6.getGhostActivityTicks();
            ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
            movementDelayMillis = connectionTracker.getMovementDelayMillis();
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            boolean condition3 = velocityTracker.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            condition2 = ghostActivityTicks > 3 && Math.abs(deltaY) < 0.5 && deltaYDifference < 0.05 && inOpenAir && !condition3;
            condition = (this.isExempt(new ExemptType[] { ExemptType.BED, ExemptType.EMPTIED_BUCKET, ExemptType.WEB, ExemptType.LAGGED_NEAR_GROUND, ExemptType.SLEEPING, ExemptType.JOINED_CHUNK_LOAD, ExemptType.ILLEGAL_BLOCK, ExemptType.BOAT, ExemptType.SHULKER, ExemptType.BLOCK_PLACE, ExemptType.LAGGED_NEAR_GROUND_MODERN, ExemptType.TRAPDOOR, ExemptType.WORLD_CHANGE, ExemptType.NOT_MOVING, ExemptType.LILY_PAD, ExemptType.SPECTATOR, ExemptType.SHULKER_BOX, ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.HONEY, ExemptType.CANCELLED_PLACE, ExemptType.FENCE, ExemptType.SWEET_BERRIES, ExemptType.DEAD })) || ((this.playerData.getCollisionState().isExemptFlight()) || ((this.playerData.getCollisionState().isExemptCreative()) || ((this.playerData.getCollisionState().isExemptJoined()) || ((this.playerData.getCollisionState().isExemptLiquid()) || ((this.playerData.getCollisionState().isExemptLevitation()) || ((this.playerData.getCollisionState().isExemptSlowFalling()) || ((this.playerData.getCollisionState().isExemptRiptide()) || ((this.playerData.getCollisionState().isExemptVehicle()) || ((this.playerData.getCollisionState().isExemptLenientScaffolding()) || ((this.playerData.getCollisionState().isExemptTeleport()) || ((this.playerData.getCollisionState().isExemptEnderPearl()) || ((this.playerData.getCollisionState().isExemptChunk()) || ((this.playerData.getCollisionState().isExemptComboMode()) || ((this.playerData.getCollisionState().isExemptMythicMob()) || ((this.playerData.getCollisionState().isExemptClimbable()) || ((this.playerData.getCollisionState().hasSlabAbove()) || (!((this.playerData.getVelocityTracker()).getTicksSinceExplosion() >= 20) || (!((this.playerData.getVelocityTracker()).getTicksSinceVelocity() >= 20)))))))))))))))))));
            boolean condition4 = condition;
            MotionState motionState2 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState2.getTicksSinceServerPosition();
            if (condition2 && !condition4) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    StringBuilder stringBuilder = new StringBuilder().append("ticks=").append(ghostActivityTicks).append(" acceleration=").append(deltaYDifference).append(" deltaY=").append(deltaY).append(" delay=").append(movementDelayMillis).append(" tp=").append(ticksSinceServerPosition).append(" vel=");
                    VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
                    this.fail(stringBuilder.append(velocityTracker4.getTicksSinceVelocity()).toString());
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
