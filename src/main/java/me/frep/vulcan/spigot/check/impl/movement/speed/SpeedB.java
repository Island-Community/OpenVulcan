package me.frep.vulcan.spigot.check.impl.movement.speed;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Speed", type = 'B', complexType = "Ground", description = "Moving too quickly on the ground.")
public class SpeedB extends AbstractCheck {

    public SpeedB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getJumpBoostAmplifier() > 50) {
                return;
            }
            condition = !((!this.playerData.getCollisionState().isFullyStuck()) && (!this.playerData.getCollisionState().isPartiallyStuck()));
            boolean condition2 = condition;
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            int groundTicks = this.playerData.getCollisionState().getGroundTicks();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            int ticksSinceVerticalCollision = collisionState3.getTicksSinceVerticalCollision();
            int ticksSinceIceContact = this.playerData.getCollisionState().getTicksSinceIceContact();
            CollisionState collisionState4 = this.playerData.getCollisionState();
            int ticksSinceSlime = collisionState4.getTicksSinceSlime();
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            int ticksSinceVelocity = velocityTracker.getTicksSinceVelocity();
            int ticksSinceTrapdoor = this.playerData.getCollisionState().getTicksSinceTrapdoor();
            CollisionState collisionState5 = this.playerData.getCollisionState();
            int ticksSinceFence = collisionState5.getTicksSinceFence();
            int ticksSinceBed = this.playerData.getCollisionState().getTicksSinceBed();
            boolean recentVerticalCollision = ticksSinceVerticalCollision < 45;
            boolean recentIceContact = ticksSinceIceContact < 60;
            boolean recentBed = ticksSinceBed < 30;
            CollisionState collisionState6 = this.playerData.getCollisionState();
            boolean stairs = collisionState6.isStairs();
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
            switch(groundTicks) {
                case 0:
                    max2 += 0.323;
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.076;
                    }
                    if (recentIceContact) {
                        max2 += 0.08;
                    }
                    if (stairs) {
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
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.0728;
                    }
                    if (recentIceContact) {
                        max2 += 0.1;
                    }
                    if (stairs) {
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
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.06694;
                    }
                    if (recentIceContact) {
                        max2 += 0.08;
                    }
                    if (recentBed) {
                        max2 += 0.04;
                    }
                    if (stairs) {
                        max2 += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max2 += 0.4;
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
                    if (ticksSinceIceContact == 0 && slab) {
                        max2 += 0.005;
                    }
                    if (recentFence) {
                        max2 += 0.1;
                    }
                    break;
                case 3:
                    max2 += 0.0693;
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.036;
                    }
                    if (recentIceContact) {
                        max2 += 0.045;
                    }
                    if (stairs) {
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
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.0211;
                    }
                    if (recentIceContact) {
                        max2 += 0.03;
                    }
                    if (stairs) {
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
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.0231;
                    }
                    if (recentIceContact) {
                        max2 += 0.03;
                    }
                    if (stairs) {
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
                    if (recentVerticalCollision && !condition2) {
                        max2 += 0.0027;
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
                if (groundTicks > 2 && groundTicks < 9) {
                    max2 += 0.09;
                } else if (groundTicks >= 9 && groundTicks < 15) {
                    max2 += 0.09;
                } else if (groundTicks >= 15) {
                    max2 += 0.09;
                }
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FARMLAND })) {
                max2 += 0.05;
            }
            if (ticksSinceIceContact < 30 && groundTicks >= 5 && groundTicks <= 25 && recentVerticalCollision) {
                max2 += 0.15;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CANCELLED_PLACE })) {
                max2 += 0.08;
            }
            if (ticksSinceIceContact < 30 && groundTicks > 8) {
                max2 += 0.15;
            }
            if (exempt) {
                max2 += 0.1;
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
            CollisionState collisionState9 = this.playerData.getCollisionState();
            if (collisionState9.getTicksSinceNearbySlime() < 25) {
                max2++;
            }
            if (ExemptionManager.isAtLeast1_9() && VulcanSettings.settingsEntityCollision) {
                CollisionState collisionState10 = this.playerData.getCollisionState();
                if (collisionState10.getTicksSinceEntityCollision() < 20) {
                    max2 += 0.1;
                }
            }
            MotionState motionState4 = this.playerData.getMotionState();
            if (motionState4.getTicksSinceIcePlace() < 35) {
                max2 += 0.2;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.PISTON })) {
                max2 += 0.325;
            }
            CollisionState collisionState11 = this.playerData.getCollisionState();
            if (collisionState11.getTicksSinceNearbySlab() < 5) {
                max2 += 0.025;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SNOW })) {
                max2 += 0.05;
            }
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceRavagerDamage() < 30) {
                max2 += 2.0;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.getTicksSinceCrystalDamage() < 5) {
                max2++;
            } else {
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.getTicksSinceCrystalDamage() > 5) {
                    MotionState motionState8 = this.playerData.getMotionState();
                    if (motionState8.getTicksSinceCrystalDamage() < 25) {
                        max2 += 0.75;
                    }
                }
            }
            CollisionState collisionState12 = this.playerData.getCollisionState();
            if (collisionState12.isLantern()) {
                max2 += 0.02;
            }
            CollisionState collisionState13 = this.playerData.getCollisionState();
            if (collisionState13.isPressurePlate()) {
                max2 += 0.08;
            }
            CollisionState collisionState14 = this.playerData.getCollisionState();
            if (collisionState14.isDoor()) {
                CollisionState collisionState15 = this.playerData.getCollisionState();
                if (collisionState15.getTicksSinceVerticalCollision() < 10) {
                    max2 += 0.1;
                }
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            int ticksSinceExplosion = velocityTracker2.getTicksSinceExplosion();
            VelocityTracker velocityTracker3 = this.playerData.getVelocityTracker();
            max2 += velocityTracker3.getLastHorizontalExplosion() * Math.pow(0.915F, ticksSinceExplosion);
            CollisionState collisionState16 = this.playerData.getCollisionState();
            if (collisionState16.getTicksSinceVerticalCollision() < 10) {
                if (this.playerData.getCollisionState().getTicksSinceIceContact() < 10) {
                    max2 += 0.55;
                }
            }
            CollisionState collisionState17 = this.playerData.getCollisionState();
            if (collisionState17.getTicksSinceVerticalCollision() < 10) {
                CollisionState collisionState18 = this.playerData.getCollisionState();
                if (!collisionState18.isChain()) {
                    if (!this.playerData.getCollisionState().isConduit()) {
                        CollisionState collisionState19 = this.playerData.getCollisionState();
                        if ((collisionState19.isLantern())) {
                            max2 += 0.1;
                        }
                    } else {
                        max2 += 0.1;
                    }
                } else {
                    max2 += 0.1;
                }
            }
            CollisionState collisionState20 = this.playerData.getCollisionState();
            if (collisionState20.isGlassPane()) {
                max2 += 0.075;
            }
            MotionState motionState9 = this.playerData.getMotionState();
            if (motionState9.getGravity() > 0.2) {
                max2 += 0.025;
            }
            MotionState motionState10 = this.playerData.getMotionState();
            if (motionState10.getMovementSpeed() < 0.1 && groundTicks <= 1) {
                MotionState motionState11 = this.playerData.getMotionState();
                double movementSpeedDifference = 0.1 - motionState11.getMovementSpeed();
                max2 += movementSpeedDifference + 0.01;
            }
            MotionState motionState12 = this.playerData.getMotionState();
            if (motionState12.isSlownessEffect() && groundTicks <= 1) {
                MotionState motionState13 = this.playerData.getMotionState();
                max2 += motionState13.getSlownessAmplifier() * 0.05;
            }
            CollisionState collisionState21 = this.playerData.getCollisionState();
            if (collisionState21.getTicksSinceSpeedEffect() < 15) {
                CollisionState collisionState22 = this.playerData.getCollisionState();
                if (collisionState22.getTicksSinceSpeedEffect() < 0) {
                    MotionState motionState14 = this.playerData.getMotionState();
                    max2 += motionState14.getLastSpeedAmplifier() * 0.075;
                }
            }
            VelocityTracker velocityTracker4 = this.playerData.getVelocityTracker();
            max2 += velocityTracker4.getMaximumHorizontalVelocity() * Math.pow(0.915F, ticksSinceVelocity);
            MotionState motionState15 = this.playerData.getMotionState();
            if (motionState15.getAirDragModifier() < 1.0) {
                MotionState motionState16 = this.playerData.getMotionState();
                CollisionState collisionState23 = this.playerData.getCollisionState();
                boolean previousOnGround = collisionState23.isPreviousOnGround();
                max2 = Math.max(max2, motionState16.maximumHorizontalMotion(previousDeltaXZ, previousOnGround));
            }
            if (max2 < 0.295) {
                max2 = 0.295;
            }
            double deltaXZDifference = deltaXZ - max2;
            boolean exempt2 = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.JOINED, ExemptType.CREATIVE, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.DOLPHINS_GRACE, ExemptType.TELEPORT, ExemptType.ILLEGAL_BLOCK, ExemptType.CHUNK, ExemptType.ELYTRA, ExemptType.GLIDING, ExemptType.RIPTIDE, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.FISHING_ROD, ExemptType.ENTITY_CRAM_FIX, ExemptType.DRAGON_DAMAGE, ExemptType.DEATH, ExemptType.SLEEPING, ExemptType.ENDER_PEARL, ExemptType.FROZEN, ExemptType.CHORUS_FRUIT, ExemptType.SPECTATOR, ExemptType.WORLD_CHANGE, ExemptType.ANVIL, ExemptType.CANCELLED_MOVE });
            boolean condition11 = deltaXZDifference > VulcanSettings.checksMovementSpeedBMinDifference && groundTicks >= 0;
            if (condition11 && !exempt2) {
                if (!(this.increaseBuffer() > this.maximumBuffer)) {
                    if (!(deltaXZDifference > 0.5) || !(deltaXZDifference < 100.0)) {
                        return;
                    }
                    if (this.isExempt(new ExemptType[] { ExemptType.SERVER_POSITION, ExemptType.CHORUS_FRUIT })) {
                        return;
                    }
                }
                this.fail("speed=" + deltaXZ + " max=" + max2 + " diff=" + deltaXZDifference + " ticks=" + groundTicks + " deltaY=" + deltaY);
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
