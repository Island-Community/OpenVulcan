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

@CheckInfo(name = "Flight", type = 'D', complexType = "Glide", description = "Invalid Y-Axis movement.")
public class FlightD extends AbstractCheck {

    public FlightD(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            if (VulcanSettings.checksMovementFlightDIgnoreGhostBlocks) {
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
            CollisionState collisionState6 = this.playerData.getCollisionState();
            int ghostActivityTicks = collisionState6.getGhostActivityTicks();
            CollisionState collisionState7 = this.playerData.getCollisionState();
            double deltaY = collisionState7.getDeltaY();
            if (this.playerData.getMotionState().getTicksSinceDragonDamage() < 200) {
                return;
            }
            if (deltaY + 0.09800000190734881 <= 0.001) {
                return;
            }
            MotionState motionState2 = this.playerData.getMotionState();
            if (motionState2.getJumpStrength() > 2.0) {
                return;
            }
            MotionState motionState3 = this.playerData.getMotionState();
            if (motionState3.getTicksSinceGravityChange() < 20) {
                return;
            }
            boolean recentExplosion;
            boolean recentBed;
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            recentExplosion = velocityTracker.getTicksSinceExplosion() < 100;
            recentBed = this.playerData.getCollisionState().getTicksSinceBed() < 30;
            condition = !((!this.isExempt(new ExemptType[] { ExemptType.LAGGED_NEAR_GROUND, ExemptType.DEAD, ExemptType.WEB, ExemptType.JUMP_BOOST_RAN_OUT, ExemptType.EMPTIED_BUCKET, ExemptType.WORLD_CHANGE, ExemptType.ILLEGAL_BLOCK, ExemptType.BOAT, ExemptType.LIQUID, ExemptType.NOT_MOVING, ExemptType.FENCE, ExemptType.SHULKER, ExemptType.TRAPDOOR, ExemptType.SHULKER_BOX, ExemptType.NEAR_GROUND, ExemptType.FISHING_ROD, ExemptType.LAGGED_NEAR_GROUND_MODERN, ExemptType.VOID, ExemptType.CANCELLED_PLACE, ExemptType.SPECTATOR, ExemptType.SERVER_POSITION, ExemptType.PISTON, ExemptType.HIGH_JUMP_BOOST, ExemptType.NEAR_GROUND, ExemptType.SLIME, ExemptType.PARTIALLY_STUCK, ExemptType.FULLY_STUCK, ExemptType.RESPAWN, ExemptType.PLACED_SLIME, ExemptType.GLIDING, ExemptType.ELYTRA })) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptJoined()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptLevitation()) && (!this.playerData.getCollisionState().isExemptSlowFalling()) && (!this.playerData.getCollisionState().isExemptRiptide()) && (!this.playerData.getCollisionState().isExemptVehicle()) && (!this.playerData.getCollisionState().isExemptLenientScaffolding()) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptEnderPearl()) && (!this.playerData.getCollisionState().isExemptChunk()) && (!this.playerData.getCollisionState().isExemptComboMode()) && (!this.playerData.getCollisionState().isExemptMythicMob()) && (!this.playerData.getCollisionState().isExemptClimbable()) && (!this.playerData.getCollisionState().hasSlabAbove()));
            boolean condition4 = condition;
            byte index = 18;
            if (this.isExempt(new ExemptType[] { ExemptType.BLOCK_PLACE, ExemptType.CANCELLED_PLACE })) {
                index += 30;
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean condition5 = velocityTracker2.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
            long movementDelayMillis = connectionTracker.getMovementDelayMillis();
            boolean condition6 = ghostActivityTicks > index && deltaY > -0.5 && !condition5;
            MotionState motionState4 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState4.getTicksSinceServerPosition();
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            int ticksSinceVelocity = velocityTracker3.getTicksSinceVelocity();
            if (condition6 && !condition4 && !recentExplosion && !recentBed && ticksSinceVelocity > 30) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaY=" + deltaY + " ticks=" + ghostActivityTicks + " delay=" + movementDelayMillis + " tp=" + ticksSinceServerPosition + " vel=" + ticksSinceVelocity);
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
