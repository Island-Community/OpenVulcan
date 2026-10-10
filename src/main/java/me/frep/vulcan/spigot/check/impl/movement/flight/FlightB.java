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
import me.frep.vulcan.spigot.util.minecraft.MaterialSets;

@CheckInfo(name = "Flight", type = 'B', complexType = "Prediction [C]", description = "Invalid Y-Axis movement.")
public class FlightB extends AbstractCheck {

    public FlightB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getJumpBoostAmplifier() > 50) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            int airTicks = collisionState.getAirTicks();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            double previousDeltaY = this.playerData.getCollisionState().getPreviousDeltaY();
            boolean belowDeltaYDifferenceLimit = Math.abs(deltaY - 0.11760000228882461) <= 0.001;
            if (belowDeltaYDifferenceLimit) {
                return;
            }
            MotionState motionState3 = this.playerData.getMotionState();
            double scaledPreviousDeltaYDifference = (previousDeltaY - motionState3.getGravity()) * 0.98F;
            double deltaYDifference = Math.abs(deltaY - scaledPreviousDeltaYDifference);
            if (deltaY + 0.09800000190734881 <= 0.005) {
                return;
            }
            MotionState motionState4 = this.playerData.getMotionState();
            if (motionState4.getTicksSinceGravityChange() < 20) {
                return;
            }
            boolean recentVelocity;
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            recentVelocity = velocityTracker.getTicksSinceVelocity() < 20;
            condition = !((!this.isExempt(new ExemptType[] { ExemptType.BOAT, ExemptType.PARTIALLY_STUCK, ExemptType.DEAD, ExemptType.CHAIN, ExemptType.WEB, ExemptType.PLACING, ExemptType.WATERLOGGED, ExemptType.SLIME, ExemptType.WORLD_CHANGE, ExemptType.HONEY, ExemptType.FISHING_ROD, ExemptType.EMPTIED_BUCKET, ExemptType.SWEET_BERRIES, ExemptType.BUBBLE_COLUMN, ExemptType.SCAFFOLDING, ExemptType.NOT_MOVING, ExemptType.SEAGRASS, ExemptType.SIGN, ExemptType.ANVIL, ExemptType.END_ROD, ExemptType.SPECTATOR, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.FENCE, ExemptType.KELP, ExemptType.BLOCK_BREAK, ExemptType.FENCE_GATE, ExemptType.JOINED_CHUNK_LOAD, ExemptType.DOLPHINS_GRACE, ExemptType.PLACED_WEB, ExemptType.SEAGRASS, ExemptType.WALL, ExemptType.SEA_PICKLE, ExemptType.CHEST, ExemptType.DRIPSTONE, ExemptType.POWDER_SNOW, ExemptType.FULLY_STUCK, ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.BED, ExemptType.SLEEPING, ExemptType.CANCELLED_MOVE, ExemptType.SIGN, ExemptType.CONDUIT })) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptJoined()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptLevitation()) && (!this.playerData.getCollisionState().isExemptSlowFalling()) && (!this.playerData.getCollisionState().isExemptRiptide()) && (!this.playerData.getCollisionState().isExemptVehicle()) && (!this.playerData.getCollisionState().isExemptLenientScaffolding()) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptEnderPearl()) && (!this.playerData.getCollisionState().isExemptChunk()) && (!this.playerData.getCollisionState().isExemptComboMode()) && (!this.playerData.getCollisionState().isExemptMythicMob()) && (!this.playerData.getCollisionState().isExemptClimbable()) && (!this.playerData.getCollisionState().isNearCandle()) && (!this.playerData.getCollisionState().hasSlabAbove()) && (!this.playerData.getCollisionState().isNearDriedGhast()));
            boolean condition4 = condition;
            CollisionState collisionState3 = this.playerData.getCollisionState();
            double deltaXZ = collisionState3.getDeltaXZ();
            boolean nearGround = this.playerData.getCollisionState().isNearGround();
            if (VulcanSettings.ghostBlocksFixGhostWaterFix && deltaXZ < 0.15 && nearGround) {
                for (double amount3 : MaterialSets.jumpMotionValues) {
                    if (Math.abs(deltaY) > 0.0 && Math.abs(deltaY - amount3) < 0.01) {
                        return;
                    }
                }
            }
            int index = 7;
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.isPacketJumpBoostEffect()) {
                MotionState motionState6 = this.playerData.getMotionState();
                index += motionState6.getJumpBoostAmplifier();
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean condition5 = velocityTracker2.getTicksSinceUnloadedPosition() < 50 && deltaY >= -1.25 && deltaY <= 1.25;
            boolean condition6 = airTicks > index && deltaYDifference > 1.0E-8 && !condition5;
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            int ticksSinceVelocity = velocityTracker3.getTicksSinceVelocity();
            MotionState motionState7 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState7.getTicksSinceServerPosition();
            ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
            long movementDelayMillis = connectionTracker.getMovementDelayMillis();
            VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
            boolean pendingVelocity = velocityTracker4.isPendingVelocity();
            if (condition6 && !condition4 && !recentVelocity && !pendingVelocity) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaY=" + deltaY + " difference=" + deltaYDifference + " ticks=" + airTicks + " delay=" + movementDelayMillis + " tp=" + ticksSinceServerPosition + " vel=" + ticksSinceVelocity);
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
