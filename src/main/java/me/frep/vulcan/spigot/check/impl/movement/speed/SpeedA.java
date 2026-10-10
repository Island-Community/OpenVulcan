package me.frep.vulcan.spigot.check.impl.movement.speed;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Speed", type = 'A', complexType = "Friction", description = "Invalid friction.")
public class SpeedA extends AbstractCheck {

    public SpeedA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getTicksSinceCrystalDamage() < 3) {
                return;
            }
            double deltaXZ;
            double difference;
            int airTicks;
            boolean recentNearbyPiston;
            boolean recentVelocity;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaXZ = collisionState.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            MotionState motionState2 = this.playerData.getMotionState();
            double adjustedScaledPreviousDeltaXZ = previousDeltaXZ * motionState2.applyAirDrag(0.91F) + 0.026;
            difference = deltaXZ - adjustedScaledPreviousDeltaXZ;
            CollisionState collisionState2 = this.playerData.getCollisionState();
            airTicks = collisionState2.getAirTicks();
            recentNearbyPiston = this.playerData.getCollisionState().getTicksSinceNearbyPiston() < 15;
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            recentVelocity = velocityTracker.getTicksSinceVelocity() < 3;
            condition = !((!this.isExempt(new ExemptType[] { ExemptType.ILLEGAL_BLOCK, ExemptType.FULLY_STUCK, ExemptType.DOLPHINS_GRACE, ExemptType.WEB, ExemptType.SLEEPING, ExemptType.GLIDING, ExemptType.ELYTRA, ExemptType.DEPTH_STRIDER, ExemptType.VOID, ExemptType.WORLD_CHANGE, ExemptType.CHUNK, ExemptType.JOINED, ExemptType.ENTITY_COLLISION, ExemptType.ANVIL, ExemptType.TRAPDOOR, ExemptType.SPECTATOR, ExemptType.PARTIALLY_STUCK, ExemptType.FENCE_GATE, ExemptType.FROZEN, ExemptType.CHORUS_FRUIT, ExemptType.BLOCK_BREAK, ExemptType.CANCELLED_MOVE })) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptRiptide()) && (!this.playerData.getCollisionState().isExemptEnderPearl()) && (!this.playerData.getCollisionState().isExemptVehicle()));
            boolean condition4 = condition;
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean recentExplosion = velocityTracker2.getTicksSinceExplosion() < 5;
            if (airTicks > 2 && !recentVelocity && !condition4 && !recentNearbyPiston && deltaXZ > 0.25 && !recentExplosion) {
                if (difference > 1.0E-5) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("difference=" + difference + " ticks=" + airTicks + " deltaXZ=" + deltaXZ);
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
