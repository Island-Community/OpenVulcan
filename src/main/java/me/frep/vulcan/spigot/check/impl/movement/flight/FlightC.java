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

@CheckInfo(name = "Flight", type = 'C', complexType = "Ascension", description = "Invalid Y-Axis movement.")
public class FlightC extends AbstractCheck {

    public FlightC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        boolean condition2;
        boolean condition3;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            if (VulcanSettings.checksMovementFlightCIgnoreGhostBlocks) {
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
            if (VulcanSettings.ghostBlocksFixIgnoreLegacyVia) {
                CollisionState collisionState4 = this.playerData.getCollisionState();
                if (collisionState4.getY() < 5.0) {
                    CollisionState collisionState5 = this.playerData.getCollisionState();
                    if (collisionState5.getY() > -5.0) {
                        return;
                    }
                }
            }
            if (this.playerData.getMotionState().getTicksSinceDragonDamage() < 200) {
                return;
            }
            CollisionState collisionState6 = this.playerData.getCollisionState();
            int ghostActivityTicks = collisionState6.getGhostActivityTicks();
            CollisionState collisionState7 = this.playerData.getCollisionState();
            double deltaY = collisionState7.getDeltaY();
            byte index = 7;
            if (this.isExempt(new ExemptType[] { ExemptType.BLOCK_PLACE, ExemptType.CANCELLED_PLACE })) {
                index += 15;
            }
            if (Math.abs(deltaY) - 0.08307781780646728 < 0.001) {
                return;
            }
            boolean condition4;
            boolean recentBed;
            boolean recentExplosion;
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            boolean condition7 = velocityTracker.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            condition4 = ghostActivityTicks > index && deltaY > 0.0 && !condition7;
            recentBed = this.playerData.getCollisionState().getTicksSinceBed() < 30;
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            recentExplosion = velocityTracker2.getTicksSinceExplosion() < 100;
            CollisionState collisionState8 = this.playerData.getCollisionState();
            condition3 = !((collisionState8.getTicksSinceSlime() >= 80) && (this.playerData.getMotionState().getTicksSinceSlimePlace() >= 80));
            boolean condition8;
            condition8 = condition3;
            condition2 = !((!this.isExempt(new ExemptType[] { ExemptType.SHULKER_BOX, ExemptType.BOAT, ExemptType.WORLD_CHANGE, ExemptType.JUMP_BOOST, ExemptType.FENCE, ExemptType.LAGGED_NEAR_GROUND, ExemptType.ILLEGAL_BLOCK, ExemptType.SHULKER, ExemptType.FROZEN, ExemptType.FISHING_ROD, ExemptType.CANCELLED_PLACE, ExemptType.NOT_MOVING, ExemptType.SPECTATOR, ExemptType.CHORUS_FRUIT, ExemptType.PISTON, ExemptType.NEAR_GROUND, ExemptType.LAGGED_NEAR_GROUND_MODERN, ExemptType.EMPTIED_BUCKET, ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.BOAT, ExemptType.PLACED_SLIME, ExemptType.DEAD })) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptJoined()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptLevitation()) && (!this.playerData.getCollisionState().isExemptSlowFalling()) && (!this.playerData.getCollisionState().isExemptRiptide()) && (!this.playerData.getCollisionState().isExemptVehicle()) && (!this.playerData.getCollisionState().isExemptLenientScaffolding()) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptEnderPearl()) && (!this.playerData.getCollisionState().isExemptChunk()) && (!this.playerData.getCollisionState().isExemptComboMode()) && (!this.playerData.getCollisionState().isExemptMythicMob()) && (!this.playerData.getCollisionState().isExemptClimbable()) && (!this.playerData.getCollisionState().hasSlabAbove()));
            boolean condition9;
            long movementDelayMillis;
            condition9 = condition2;
            ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
            movementDelayMillis = connectionTracker.getMovementDelayMillis();
            condition = !(this.playerData.getMotionState().getTicksSinceKnockbackDamage() >= 40 || ghostActivityTicks >= 30) || (!((this.playerData.getVelocityTracker()).getTicksSinceVelocity() >= 40));
            boolean condition10 = condition;
            MotionState motionState2 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState2.getTicksSinceServerPosition();
            if (condition4 && !condition9 && !condition8 && !condition10 && !recentBed && !recentExplosion) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    StringBuilder stringBuilder = new StringBuilder().append("deltaY=").append(deltaY).append(" ticks=").append(ghostActivityTicks).append(" delay=").append(movementDelayMillis).append(" tp=").append(ticksSinceServerPosition).append(" v=");
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
