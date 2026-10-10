package me.frep.vulcan.spigot.check.impl.movement.speed;

import com.github.retrooper.packetevents.event.PacketEvent;
import java.util.UUID;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Speed", type = 'C', complexType = "Air", description = "Moving too quickly in the air.")
public class SpeedC extends AbstractCheck {

    public final UUID uniqueId;

    public SpeedC(PlayerData playerData) {
        super(playerData);
        this.uniqueId = UUID.fromString("662a6b8d-da3e-4c1c-8813-96ea6097278d");
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getJumpBoostAmplifier() > 50) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            int airTicks = collisionState3.getAirTicks();
            boolean recentIceContact = this.playerData.getCollisionState().getTicksSinceIceContact() < 40;
            CollisionState collisionState4 = this.playerData.getCollisionState();
            boolean recentVerticalCollision = collisionState4.getTicksSinceVerticalCollision() < 30;
            CollisionState collisionState5 = this.playerData.getCollisionState();
            boolean recentSlime = collisionState5.getTicksSinceSlime() < 30;
            boolean recentSoulSpeed = this.playerData.getCollisionState().getTicksSinceSoulSpeed() < 30;
            double amount = 0.3625;
            MotionState motionState3 = this.playerData.getMotionState();
            double movementSpeed = motionState3.getMovementSpeed();
            double scaledMovementSpeed = movementSpeed / 0.1;
            amount *= scaledMovementSpeed;
            if (recentVerticalCollision) {
                amount += 0.1;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CANCELLED_PLACE })) {
                amount += 0.1;
            }
            CollisionState collisionState6 = this.playerData.getCollisionState();
            if (collisionState6.getTicksSinceNearbySlime() < 15) {
                amount += 0.6;
            }
            if (airTicks == 2) {
                CollisionState collisionState7 = this.playerData.getCollisionState();
                if (collisionState7.getTicksSinceVerticalCollision() < 50 && deltaY == -0.07840000152587834) {
                    amount += 0.165;
                }
            }
            if (recentIceContact) {
                amount += 0.25;
            }
            if (recentVerticalCollision && recentIceContact) {
                amount += 0.3;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.STAIRS })) {
                amount += 0.05;
            }
            if (recentSlime) {
                amount += 0.1;
            }
            if (ExemptionManager.isAtLeast1_16() && recentSoulSpeed) {
                amount += 0.3;
            }
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            int ticksSinceVelocity = velocityTracker.getTicksSinceVelocity();
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            amount += velocityTracker2.getMaximumHorizontalVelocity() * Math.pow(0.915F, ticksSinceVelocity);
            if (ticksSinceVelocity < 10) {
                amount += 0.025;
            }
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            int ticksSinceExplosion = velocityTracker3.getTicksSinceExplosion();
            VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
            amount += velocityTracker4.getLastHorizontalExplosion() * Math.pow(0.915F, ticksSinceExplosion);
            if (ticksSinceExplosion < 10) {
                amount += 0.025;
            }
            if (ExemptionManager.isNewerThan1_13()) {
                if (this.playerData.getCollisionState().getTicksSinceHoney() < 20) {
                    amount += 0.125;
                }
            }
            MotionState motionState4 = this.playerData.getMotionState();
            if (motionState4.getTicksSinceIcePlace() < 40) {
                amount += 0.2;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FARMLAND })) {
                amount += 0.025;
            }
            if (ExemptionManager.isAtLeast1_9() && VulcanSettings.settingsEntityCollision) {
                CollisionState collisionState8 = this.playerData.getCollisionState();
                if (collisionState8.getTicksSinceEntityCollision() < 20) {
                    amount += 0.1;
                }
            }
            if (this.isExempt(new ExemptType[] { ExemptType.PISTON })) {
                amount += 0.325;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SNOW })) {
                amount += 0.05;
            }
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceFireball() < 30) {
                amount += 0.75;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.getTicksSinceCrystalDamage() < 5) {
                amount++;
            } else {
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.getTicksSinceCrystalDamage() > 5) {
                    MotionState motionState8 = this.playerData.getMotionState();
                    if (motionState8.getTicksSinceCrystalDamage() < 25) {
                        amount += 0.5;
                    }
                }
            }
            CollisionState collisionState9 = this.playerData.getCollisionState();
            if (collisionState9.getTicksSinceSpeedEffect() < 15) {
                CollisionState collisionState10 = this.playerData.getCollisionState();
                if (collisionState10.getTicksSinceSpeedEffect() < 0) {
                    MotionState motionState9 = this.playerData.getMotionState();
                    amount += motionState9.getLastSpeedAmplifier() * 0.075;
                }
            }
            if (this.playerData.getCollisionState().getTicksWithoutGliding() >= 30) {
                MotionState motionState10 = this.playerData.getMotionState();
                if (!(motionState10.getTicksSinceGliding() >= 30)) {
                    CollisionState collisionState11 = this.playerData.getCollisionState();
                    amount += collisionState11.getLastGlidingDeltaXZ() + 0.15;
                }
            } else {
                CollisionState collisionState11 = this.playerData.getCollisionState();
                amount += collisionState11.getLastGlidingDeltaXZ() + 0.15;
            }
            if (this.playerData.getMotionState().getFrictionModifier() < 1.0) {
                MotionState motionState11 = this.playerData.getMotionState();
                double adjustedScaledPreviousDeltaXZ = previousDeltaXZ * motionState11.applyAirDrag(0.91) + 0.046;
                amount = Math.max(amount, adjustedScaledPreviousDeltaXZ);
            }
            MotionState motionState12 = this.playerData.getMotionState();
            if (motionState12.getAirDragModifier() < 1.0) {
                MotionState motionState13 = this.playerData.getMotionState();
                amount = Math.max(amount, motionState13.maximumHorizontalMotion(previousDeltaXZ, false));
            }
            if (amount < 0.36) {
                amount = 0.36;
            }
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.ILLEGAL_BLOCK, ExemptType.RIPTIDE, ExemptType.SHULKER, ExemptType.CHORUS_FRUIT, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.SHULKER_BOX, ExemptType.CHUNK, ExemptType.DRAGON_DAMAGE, ExemptType.FISHING_ROD, ExemptType.DOLPHINS_GRACE, ExemptType.ENDER_PEARL, ExemptType.TELEPORT, ExemptType.WORLD_CHANGE, ExemptType.FROZEN, ExemptType.DEATH, ExemptType.SLEEPING, ExemptType.BOAT, ExemptType.SPECTATOR, ExemptType.GLIDING, ExemptType.ANVIL, ExemptType.FULLY_STUCK, ExemptType.PARTIALLY_STUCK, ExemptType.CANCELLED_MOVE, ExemptType.ENTITY_CRAM_FIX });
            double deltaXZDifference = deltaXZ - amount;
            boolean condition5 = airTicks > 1 && deltaXZDifference > VulcanSettings.checksMovementSpeedCMinDifference;
            if (condition5 && !exempt) {
                if (!(this.increaseBuffer() > this.maximumBuffer)) {
                    if (!(deltaXZDifference > 0.5) || !(deltaXZDifference < 100.0)) {
                        return;
                    }
                    if (this.isExempt(new ExemptType[] { ExemptType.SERVER_POSITION_FAST })) {
                        return;
                    }
                }
                StringBuilder stringBuilder = new StringBuilder().append("speed=").append(deltaXZ).append(" max=").append(amount).append(" diff=").append(deltaXZDifference).append(" ticks=").append(airTicks).append(" deltaY=").append(deltaY).append(" t=");
                MotionState motionState14 = this.playerData.getMotionState();
                this.fail(stringBuilder.append(motionState14.getTicksSinceGliding()).toString());
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
