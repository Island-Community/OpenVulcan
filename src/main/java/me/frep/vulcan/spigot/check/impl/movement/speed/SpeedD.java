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

@CheckInfo(name = "Speed", type = 'D', complexType = "Ground", description = "Moving too quickly on the ground.")
public class SpeedD extends AbstractCheck {

    public final UUID uniqueId;

    public SpeedD(PlayerData playerData) {
        super(playerData);
        this.uniqueId = UUID.fromString("662a6b8d-da3e-4c1c-8813-96ea6097278d");
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        boolean condition2;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getJumpBoostAmplifier() > 50) {
                return;
            }
            double deltaXZ;
            double previousDeltaXZ;
            double deltaY;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaXZ = collisionState.getDeltaXZ();
            previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            deltaY = collisionState2.getDeltaY();
            condition2 = !((!this.playerData.getCollisionState().isFullyStuck()) && (!this.playerData.getCollisionState().isPartiallyStuck()));
            boolean condition3;
            int groundAlignedTicks;
            int ticksSinceVerticalCollision;
            int ticksSinceIceContact;
            int ticksSinceSlime;
            int ticksSinceVelocity;
            int ticksSinceTrapdoor;
            int ticksSinceFence;
            boolean recentVerticalCollision;
            boolean recentIceContact;
            boolean recentBed;
            condition3 = condition2;
            groundAlignedTicks = this.playerData.getCollisionState().getGroundAlignedTicks();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            ticksSinceVerticalCollision = collisionState3.getTicksSinceVerticalCollision();
            ticksSinceIceContact = this.playerData.getCollisionState().getTicksSinceIceContact();
            CollisionState collisionState4 = this.playerData.getCollisionState();
            ticksSinceSlime = collisionState4.getTicksSinceSlime();
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            ticksSinceVelocity = velocityTracker.getTicksSinceVelocity();
            ticksSinceTrapdoor = this.playerData.getCollisionState().getTicksSinceTrapdoor();
            CollisionState collisionState5 = this.playerData.getCollisionState();
            ticksSinceFence = collisionState5.getTicksSinceFence();
            int ticksSinceBed = this.playerData.getCollisionState().getTicksSinceBed();
            recentVerticalCollision = ticksSinceVerticalCollision < 45;
            recentIceContact = ticksSinceIceContact < 60;
            recentBed = ticksSinceBed < 30;
            CollisionState collisionState6 = this.playerData.getCollisionState();
            condition = !((!collisionState6.isStairs()) && (this.playerData.getCollisionState().getTicksSinceStairs() >= 20));
            boolean condition7 = condition;
            boolean recentSlime = ticksSinceSlime < 30;
            boolean recentTrapdoor = ticksSinceTrapdoor < 40;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.DEPTH_STRIDER });
            CollisionState collisionState7 = this.playerData.getCollisionState();
            boolean slab = collisionState7.isSlab();
            boolean recentFence = ticksSinceFence < 20;
            boolean recentSoulSpeed = this.playerData.getCollisionState().getTicksSinceSoulSpeed() < 30;
            CollisionState collisionState8 = this.playerData.getCollisionState();
            boolean recentSlime2 = collisionState8.getTicksSinceSlime() < 30;
            double max2 = 0.2975;
            MotionState motionState3 = this.playerData.getMotionState();
            double movementSpeed = motionState3.getMovementSpeed();
            double scaledMovementSpeed = movementSpeed / 0.1;
            max2 *= scaledMovementSpeed;
            switch(groundAlignedTicks) {
                case 0:
                    max2 += 0.323;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.076;
                    }
                    if (recentIceContact) {
                        max2 += 0.08;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (recentSlime) {
                        max2 += 0.05;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.6;
                    }
                    if (slab) {
                        max2 += 0.04;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max2 += 0.1;
                    }
                    if (recentTrapdoor) {
                        max2 += 0.1;
                    }
                    if (recentFence) {
                        max2 += 0.1;
                    }
                    if (deltaY == 0.42F) {
                        max2 += 0.03;
                    }
                    break;
                case 1:
                    max2 += 0.025;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.0728;
                    }
                    if (recentIceContact) {
                        max2 += 0.1;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.45;
                    }
                    if (recentSlime) {
                        max2 += 0.05;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.55;
                    }
                    if (slab) {
                        max2 += 0.15;
                    }
                    if (recentTrapdoor) {
                        max2 += 0.1;
                    }
                    if (recentFence) {
                        max2 += 0.1;
                    }
                    break;
                case 2:
                    max2 += 0.134;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.06694;
                    }
                    if (recentIceContact) {
                        max2 += 0.08;
                    }
                    if (recentBed) {
                        max2 += 0.04;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.4;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.55;
                    }
                    if (slab) {
                        max2 += 0.05;
                    }
                    if (recentTrapdoor) {
                        max2 += 0.1;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max2 += 0.005;
                    }
                    if (recentFence) {
                        max2 += 0.1;
                    }
                    break;
                case 3:
                    max2 += 0.0693;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.036;
                    }
                    if (recentIceContact) {
                        max2 += 0.045;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (slab) {
                        max2 += 0.04;
                    }
                    if (recentBed) {
                        max2 += 0.04;
                    }
                    if (recentFence) {
                        max2 += 0.1;
                    }
                    if (recentTrapdoor) {
                        max2 += 0.1;
                    }
                    break;
                case 4:
                    max2 += 0.035;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.0411;
                    }
                    if (recentIceContact) {
                        max2 += 0.03;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentTrapdoor) {
                        max2 += 0.1;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (slab) {
                        max2 += 0.04;
                    }
                    break;
                case 5:
                    max2 += 0.0205;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.0431;
                    }
                    if (recentIceContact) {
                        max2 += 0.03;
                    }
                    if (condition7) {
                        max2 += 0.35;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (slab) {
                        max2 += 0.04;
                    }
                    break;
                case 6:
                    max2 += 0.009;
                    if (recentVerticalCollision && !condition3) {
                        max2 += 0.0527;
                    }
                    if (recentIceContact) {
                        max2 += 0.02;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.35;
                    }
                    if (slab) {
                        max2 += 0.04;
                    }
                    break;
                case 7:
                    max2 += 0.005;
                    if (recentIceContact) {
                        max2 += 0.02;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max2 += 0.1;
                    }
                    break;
                case 8:
                    max2 += 0.003;
                    if (recentIceContact) {
                        max2 += 0.02;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max2 += 0.02;
                    }
                    break;
                case 9:
                    max2 += 0.0025;
                    if (recentIceContact) {
                        max2 += 0.02;
                    }
            }
            if (ticksSinceIceContact == 0) {
                if (groundAlignedTicks > 2 && groundAlignedTicks < 9) {
                    max2 += 0.09;
                } else if (groundAlignedTicks >= 9 && groundAlignedTicks < 15) {
                    max2 += 0.09;
                } else if (groundAlignedTicks >= 15) {
                    max2 += 0.09;
                }
            }
            CollisionState collisionState9 = this.playerData.getCollisionState();
            if (collisionState9.getTicksSinceVerticalCollision() < 5) {
                max2 += 0.1;
            }
            if (ticksSinceVerticalCollision < 20 && groundAlignedTicks < 15 && !condition3) {
                max2 += 0.1;
            }
            if (slab) {
                max2 += 0.1;
            }
            if (ticksSinceIceContact < 30 && groundAlignedTicks >= 5 && groundAlignedTicks <= 25 && recentVerticalCollision) {
                max2 += 0.15;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CANCELLED_PLACE })) {
                max2 += 0.08;
            }
            CollisionState collisionState10 = this.playerData.getCollisionState();
            int ticksSinceSlab = collisionState10.getTicksSinceSlab();
            if (ticksSinceSlab < 20) {
                max2 += 0.075;
            }
            if (ticksSinceIceContact < 30 && groundAlignedTicks > 8) {
                max2 += 0.15;
            }
            MotionState motionState4 = this.playerData.getMotionState();
            if (motionState4.getGravity() > 0.2) {
                max2 += 0.025;
            }
            if (exempt) {
                max2 += 0.1;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FARMLAND })) {
                max2 += 0.05;
            }
            if (ExemptionManager.isAtLeast1_16() && recentSoulSpeed) {
                max2 += 0.5;
            }
            if (recentIceContact && recentSlime2) {
                max2 += 0.35;
            }
            if (recentSlime2 && recentVerticalCollision) {
                max2 += 0.1;
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            max2 += velocityTracker2.getMaximumHorizontalVelocity() * Math.pow(0.915F, ticksSinceVelocity);
            CollisionState collisionState11 = this.playerData.getCollisionState();
            if (collisionState11.getTicksSinceIce() < 40) {
                max2 += 0.125;
            }
            CollisionState collisionState12 = this.playerData.getCollisionState();
            if (collisionState12.isTrapdoor()) {
                max2 += 0.125;
            }
            CollisionState collisionState13 = this.playerData.getCollisionState();
            if (collisionState13.getTicksSinceNearbySlime() < 15) {
                max2 += 0.6;
            }
            if (ExemptionManager.isAtLeast1_9() && VulcanSettings.settingsEntityCollision) {
                CollisionState collisionState14 = this.playerData.getCollisionState();
                if (collisionState14.getTicksSinceEntityCollision() < 20) {
                    max2 += 0.1;
                }
            }
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceIcePlace() < 40) {
                max2 += 0.2;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.PISTON })) {
                max2 += 0.325;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CAKE })) {
                max2 += 0.1;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.STAIRS, ExemptType.SLAB })) {
                max2 += 0.45;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.BED })) {
                max2 += 0.25;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FENCE })) {
                max2 += 0.225;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SNOW })) {
                max2 += 0.1;
            }
            CollisionState collisionState15 = this.playerData.getCollisionState();
            if (collisionState15.isAmethyst()) {
                max2 += 0.05F;
                if (deltaY == 0.4375 || deltaY == 0.5625) {
                    max2 += 0.25;
                }
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SNOW }) && deltaY == 0.5) {
                max2 += 0.15F;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.getTicksSinceRavagerDamage() < 30) {
                max2 += 2.0;
            }
            MotionState motionState7 = this.playerData.getMotionState();
            if (motionState7.getTicksSinceCrystalDamage() < 5) {
                max2++;
            } else {
                MotionState motionState8 = this.playerData.getMotionState();
                if (motionState8.getTicksSinceCrystalDamage() > 5) {
                    MotionState motionState9 = this.playerData.getMotionState();
                    if (motionState9.getTicksSinceCrystalDamage() < 25) {
                        max2 += 0.5;
                    }
                }
            }
            CollisionState collisionState16 = this.playerData.getCollisionState();
            if (collisionState16.isPressurePlate()) {
                max2 += 0.08;
            }
            CollisionState collisionState17 = this.playerData.getCollisionState();
            if (collisionState17.isNearSculkSensor()) {
                max2 += 0.125;
            }
            CollisionState collisionState18 = this.playerData.getCollisionState();
            if (collisionState18.isDoor()) {
                CollisionState collisionState19 = this.playerData.getCollisionState();
                if (collisionState19.getTicksSinceVerticalCollision() < 10) {
                    max2 += 0.15;
                }
            }
            CollisionState collisionState20 = this.playerData.getCollisionState();
            if (collisionState20.isLantern()) {
                max2 += 0.075;
            }
            CollisionState collisionState21 = this.playerData.getCollisionState();
            if (collisionState21.isChain()) {
                max2 += 0.05;
                if (deltaY == 0.59375) {
                    max2 += 0.2;
                }
            }
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            int ticksSinceExplosion = velocityTracker3.getTicksSinceExplosion();
            VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
            max2 += velocityTracker4.getLastHorizontalExplosion() * Math.pow(0.915F, ticksSinceExplosion);
            CollisionState collisionState22 = this.playerData.getCollisionState();
            if (collisionState22.getTicksSinceVerticalCollision() < 10) {
                if (this.playerData.getCollisionState().getTicksSinceIceContact() < 10) {
                    max2 += 0.55;
                }
            }
            CollisionState collisionState23 = this.playerData.getCollisionState();
            if (collisionState23.isGlassPane()) {
                max2 += 0.075;
            }
            MotionState motionState10 = this.playerData.getMotionState();
            if (motionState10.getMovementSpeed() < 0.1 && groundAlignedTicks <= 1) {
                MotionState motionState11 = this.playerData.getMotionState();
                double movementSpeedDifference = 0.1 - motionState11.getMovementSpeed();
                max2 += movementSpeedDifference + 0.01;
            }
            MotionState motionState12 = this.playerData.getMotionState();
            if (motionState12.isSlownessEffect() && groundAlignedTicks <= 1) {
                MotionState motionState13 = this.playerData.getMotionState();
                max2 += motionState13.getSlownessAmplifier() * 0.05;
            }
            CollisionState collisionState24 = this.playerData.getCollisionState();
            if (collisionState24.isNearHeavyCore()) {
                max2 += 0.25;
            }
            CollisionState collisionState25 = this.playerData.getCollisionState();
            if (collisionState25.getTicksSinceSpeedEffect() < 15) {
                CollisionState collisionState26 = this.playerData.getCollisionState();
                if (collisionState26.getTicksSinceSpeedEffect() < 0) {
                    MotionState motionState14 = this.playerData.getMotionState();
                    max2 += motionState14.getLastSpeedAmplifier() * 0.075;
                }
            }
            MotionState motionState15 = this.playerData.getMotionState();
            if (motionState15.getAirDragModifier() < 1.0) {
                MotionState motionState16 = this.playerData.getMotionState();
                CollisionState collisionState27 = this.playerData.getCollisionState();
                boolean previousOnGround = collisionState27.isPreviousOnGround();
                max2 = Math.max(max2, motionState16.maximumHorizontalMotion(previousDeltaXZ, previousOnGround));
            }
            if (max2 < 0.295) {
                max2 = 0.295;
            }
            double deltaXZDifference = deltaXZ - max2;
            boolean exempt2 = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.JOINED, ExemptType.CREATIVE, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.DOLPHINS_GRACE, ExemptType.TELEPORT, ExemptType.ILLEGAL_BLOCK, ExemptType.GLIDING, ExemptType.CANCELLED_MOVE, ExemptType.RIPTIDE, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.FISHING_ROD, ExemptType.ELYTRA, ExemptType.CHUNK, ExemptType.DEATH, ExemptType.SLEEPING, ExemptType.ENDER_PEARL, ExemptType.FROZEN, ExemptType.CHORUS_FRUIT, ExemptType.ENTITY_CRAM_FIX, ExemptType.SPECTATOR, ExemptType.WORLD_CHANGE, ExemptType.ANVIL, ExemptType.DRAGON_DAMAGE });
            boolean condition13 = deltaXZDifference > VulcanSettings.checksMovementSpeedDMinDifference && groundAlignedTicks >= 0;
            MotionState motionState17 = this.playerData.getMotionState();
            int ticksSinceServerPosition = motionState17.getTicksSinceServerPosition();
            if (condition13 && !exempt2) {
                if (!(this.increaseBuffer() > this.maximumBuffer)) {
                    if (!(deltaXZDifference > 0.5) || !(deltaXZDifference < 100.0)) {
                        return;
                    }
                    if (this.isExempt(new ExemptType[] { ExemptType.SERVER_POSITION })) {
                        return;
                    }
                }
                this.fail("speed=" + deltaXZ + " max=" + max2 + " diff=" + deltaXZDifference + " ticks=" + groundAlignedTicks + " deltaY=" + deltaY + " tp=" + ticksSinceServerPosition);
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
