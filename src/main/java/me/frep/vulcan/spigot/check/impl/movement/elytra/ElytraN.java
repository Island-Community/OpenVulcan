package me.frep.vulcan.spigot.check.impl.movement.elytra;

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

@CheckInfo(name = "Elytra", type = 'N', experimental = true, complexType = "Ground", description = "Moving too quickly on the ground.")
public class ElytraN extends AbstractCheck {

    public final UUID uniqueId;

    public ElytraN(PlayerData playerData) {
        super(playerData);
        this.uniqueId = UUID.fromString("662a6b8d-da3e-4c1c-8813-96ea6097278d");
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
            double max = 0.2975;
            MotionState motionState3 = this.playerData.getMotionState();
            double movementSpeed = motionState3.getMovementSpeed();
            double scaledMovementSpeed = movementSpeed / 0.1;
            max *= scaledMovementSpeed;
            switch(groundTicks) {
                case 0:
                    max += 0.323;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.076;
                    }
                    if (recentIceContact) {
                        max += 0.08;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max += 0.35;
                    }
                    if (recentSlime) {
                        max += 0.05;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.6;
                    }
                    if (slab) {
                        max += 0.04;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max += 0.1;
                    }
                    if (recentTrapdoor) {
                        max += 0.1;
                    }
                    if (recentFence) {
                        max += 0.1;
                    }
                    if (deltaY == 0.42F) {
                        max += 0.03;
                    }
                    break;
                case 1:
                    max += 0.025;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.0728;
                    }
                    if (recentIceContact) {
                        max += 0.1;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max += 0.45;
                    }
                    if (recentSlime) {
                        max += 0.05;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.55;
                    }
                    if (slab) {
                        max += 0.15;
                    }
                    if (recentTrapdoor) {
                        max += 0.1;
                    }
                    if (recentFence) {
                        max += 0.1;
                    }
                    break;
                case 2:
                    max += 0.134;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.06694;
                    }
                    if (recentIceContact) {
                        max += 0.08;
                    }
                    if (recentBed) {
                        max += 0.04;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max += 0.4;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.55;
                    }
                    if (slab) {
                        max += 0.15;
                    }
                    if (recentTrapdoor) {
                        max += 0.1;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max += 0.005;
                    }
                    if (recentFence) {
                        max += 0.1;
                    }
                    break;
                case 3:
                    max += 0.0693;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.036;
                    }
                    if (recentIceContact) {
                        max += 0.045;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max += 0.35;
                    }
                    if (slab) {
                        max += 0.04;
                    }
                    if (recentBed) {
                        max += 0.04;
                    }
                    if (recentFence) {
                        max += 0.1;
                    }
                    if (recentTrapdoor) {
                        max += 0.1;
                    }
                    break;
                case 4:
                    max += 0.035;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.0211;
                    }
                    if (recentIceContact) {
                        max += 0.03;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentTrapdoor) {
                        max += 0.1;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.35;
                    }
                    if (recentVerticalCollision && recentIceContact) {
                        max += 0.35;
                    }
                    if (slab) {
                        max += 0.04;
                    }
                    break;
                case 5:
                    max += 0.0205;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.0231;
                    }
                    if (recentIceContact) {
                        max += 0.03;
                    }
                    if (stairs) {
                        max += 0.35;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.35;
                    }
                    if (slab) {
                        max += 0.04;
                    }
                    break;
                case 6:
                    max += 0.009;
                    if (recentVerticalCollision && !condition2) {
                        max += 0.0027;
                    }
                    if (recentIceContact) {
                        max += 0.02;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.35;
                    }
                    if (slab) {
                        max += 0.04;
                    }
                    break;
                case 7:
                    max += 0.005;
                    if (recentIceContact) {
                        max += 0.02;
                    }
                    if (recentTrapdoor && recentIceContact) {
                        max += 0.1;
                    }
                    break;
                case 8:
                    max += 0.003;
                    if (recentIceContact) {
                        max += 0.02;
                    }
                    if (ticksSinceIceContact == 0 && slab) {
                        max += 0.02;
                    }
                    break;
                case 9:
                    max += 0.0025;
                    if (recentIceContact) {
                        max += 0.02;
                    }
            }
            if (ticksSinceIceContact == 0) {
                if (groundTicks > 2 && groundTicks < 9) {
                    max += 0.09;
                } else if (groundTicks >= 9 && groundTicks < 15) {
                    max += 0.09;
                } else if (groundTicks >= 15) {
                    max += 0.09;
                }
            }
            if (this.isExempt(new ExemptType[] { ExemptType.FARMLAND })) {
                max += 0.05;
            }
            if (ticksSinceIceContact < 30 && groundTicks >= 5 && groundTicks <= 25 && recentVerticalCollision) {
                max += 0.15;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CANCELLED_PLACE })) {
                max += 0.08;
            }
            if (ticksSinceIceContact < 30 && groundTicks > 8) {
                max += 0.15;
            }
            if (exempt) {
                max += 0.1;
            }
            if (ExemptionManager.isAtLeast1_16() && recentSoulSpeed) {
                MotionState motionState4 = this.playerData.getMotionState();
                if (motionState4.getSpeedAmplifier() > 3) {
                    max++;
                }
            }
            if (ExemptionManager.isAtLeast1_16() && recentSoulSpeed) {
                max += 0.5;
            }
            if (recentIceContact && recentSlime2) {
                max += 0.35;
            }
            if (recentSlime2 && recentVerticalCollision) {
                max += 0.1;
            }
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            double maximumHorizontalVelocity = velocityTracker2.getMaximumHorizontalVelocity();
            switch(ticksSinceVelocity) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    max += maximumHorizontalVelocity + 0.05;
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                    max += maximumHorizontalVelocity * 0.75;
                    break;
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                    max += maximumHorizontalVelocity * 0.6;
                    break;
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                    max += maximumHorizontalVelocity * 0.5;
                    break;
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                    max += maximumHorizontalVelocity * 0.35;
                    break;
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                    max += maximumHorizontalVelocity * 0.25;
                    break;
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                    max += maximumHorizontalVelocity * 0.1;
            }
            CollisionState collisionState9 = this.playerData.getCollisionState();
            if (collisionState9.getTicksSinceNearbySlime() < 25) {
                max++;
            }
            if (ExemptionManager.isAtLeast1_9() && VulcanSettings.settingsEntityCollision) {
                CollisionState collisionState10 = this.playerData.getCollisionState();
                if (collisionState10.getTicksSinceEntityCollision() < 20) {
                    max += 0.1;
                }
            }
            MotionState motionState5 = this.playerData.getMotionState();
            if (motionState5.getTicksSinceIcePlace() < 35) {
                max += 0.2;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.PISTON })) {
                max += 0.325;
            }
            CollisionState collisionState11 = this.playerData.getCollisionState();
            if (collisionState11.getTicksSinceNearbySlab() < 5) {
                max += 0.025;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.SNOW })) {
                max += 0.05;
            }
            MotionState motionState6 = this.playerData.getMotionState();
            if (motionState6.getTicksSinceRavagerDamage() < 30) {
                max += 2.0;
            }
            MotionState motionState7 = this.playerData.getMotionState();
            if (motionState7.getTicksSinceCrystalDamage() < 5) {
                max++;
            } else {
                MotionState motionState8 = this.playerData.getMotionState();
                if (motionState8.getTicksSinceCrystalDamage() > 5) {
                    MotionState motionState9 = this.playerData.getMotionState();
                    if (motionState9.getTicksSinceCrystalDamage() < 25) {
                        max += 0.75;
                    }
                }
            }
            CollisionState collisionState12 = this.playerData.getCollisionState();
            if (collisionState12.isPressurePlate()) {
                max += 0.08;
            }
            CollisionState collisionState13 = this.playerData.getCollisionState();
            if (collisionState13.isDoor()) {
                CollisionState collisionState14 = this.playerData.getCollisionState();
                if (collisionState14.getTicksSinceVerticalCollision() < 10) {
                    max += 0.1;
                }
            }
            if (max < 0.295) {
                max = 0.295;
            }
            if (this.playerData.getCollisionState().getTicksWithoutGliding() >= 20) {
                MotionState motionState10 = this.playerData.getMotionState();
                if (!(motionState10.getTicksSinceGliding() >= 20)) {
                    if (groundTicks < 5) {
                        CollisionState collisionState15 = this.playerData.getCollisionState();
                        max += collisionState15.getLastGlidingDeltaXZ();
                    }
                }
            } else if (groundTicks < 5) {
                CollisionState collisionState15 = this.playerData.getCollisionState();
                max += collisionState15.getLastGlidingDeltaXZ();
            }
            CollisionState collisionState16 = this.playerData.getCollisionState();
            if (collisionState16.getTicksSinceSpeedEffect() < 10) {
                CollisionState collisionState17 = this.playerData.getCollisionState();
                if (collisionState17.getTicksSinceSpeedEffect() > 0) {
                    MotionState motionState11 = this.playerData.getMotionState();
                    max += motionState11.getLastSpeedAmplifier() * 0.075;
                }
            }
            CollisionState collisionState18 = this.playerData.getCollisionState();
            if (collisionState18.getTicksSinceFirework() < 20 && groundTicks < 5) {
                max++;
            }
            double deltaXZDifference = deltaXZ - max;
            boolean exempt2 = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.JOINED, ExemptType.CREATIVE, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.DOLPHINS_GRACE, ExemptType.TELEPORT, ExemptType.ILLEGAL_BLOCK, ExemptType.CHUNK, ExemptType.RIPTIDE, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.FISHING_ROD, ExemptType.ENTITY_CRAM_FIX, ExemptType.DEATH, ExemptType.SLEEPING, ExemptType.ENDER_PEARL, ExemptType.FROZEN, ExemptType.CHORUS_FRUIT, ExemptType.SPECTATOR, ExemptType.WORLD_CHANGE, ExemptType.ANVIL, ExemptType.CANCELLED_MOVE });
            boolean condition11 = deltaXZDifference > 0.01 && groundTicks >= 0;
            MotionState motionState12 = this.playerData.getMotionState();
            boolean wearingElytra = motionState12.isWearingElytra();
            if (condition11 && !exempt2 && wearingElytra) {
                if (!(this.increaseBuffer() > this.maximumBuffer)) {
                    if (!(deltaXZDifference > 0.5) || !(deltaXZDifference < 100.0)) {
                        return;
                    }
                    if (this.isExempt(new ExemptType[] { ExemptType.SERVER_POSITION, ExemptType.CHORUS_FRUIT })) {
                        return;
                    }
                }
                this.fail("speed=" + deltaXZ + " max=" + max + " diff=" + deltaXZDifference + " ticks=" + groundTicks + " deltaY=" + deltaY);
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
