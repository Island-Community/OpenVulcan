package me.frep.vulcan.spigot.check.impl.movement.entityspeed;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Pig;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Entity Speed", type = 'A', complexType = "Limit", description = "Riding an entity too quickly.")
public class EntitySpeedA extends AbstractCheck {

    private int ticksSinceVehicleSpeedEffect;

    public EntitySpeedA(PlayerData playerData) {
        super(playerData);
        this.ticksSinceVehicleSpeedEffect = 10000;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isVehicleMovePacket(packetEvent)) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            if (this.playerData.getPlayer() == null || PluginServices.INSTANCE.isFoliaServer()) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            boolean recentSurroundingLiquid = collisionState.getTicksSinceSurroundingLiquid() < 100;
            boolean recentSurroundingSlime = this.playerData.getCollisionState().getTicksSinceSurroundingSlime() < 100;
            boolean recentSurroundingBubbleColumn = this.playerData.getCollisionState().getTicksSinceSurroundingBubbleColumn() < 100;
            boolean recentSurroundingIce = this.playerData.getCollisionState().getTicksSinceSurroundingIce() < 100;
            boolean recentSurroundingPiston = this.playerData.getCollisionState().getTicksSinceSurroundingPiston() < 40;
            if (recentSurroundingPiston) {
                return;
            }
            int ticksInVehicle = this.playerData.getCollisionState().getTicksInVehicle();
            double horizontalSpeed = this.playerData.getCollisionState().getVehicleHorizontalSpeed();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean alignedVehicleY = collisionState2.getVehicleY() % 0.015625 == 0.0;
            if (this.playerData.getPlayer().getVehicle() instanceof LivingEntity) {
                LivingEntity vehicle = (LivingEntity) this.playerData.getPlayer().getVehicle();
                if (CheckUtils.getSpeedPotionLevel(vehicle) > 0) {
                    this.ticksSinceVehicleSpeedEffect = 0;
                } else {
                    this.ticksSinceVehicleSpeedEffect++;
                }
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Horse)) {
                Horse vehicle2 = (Horse) this.playerData.getPlayer().getVehicle();
                if (vehicle2 == null || vehicle2.hasMetadata("PROCOSMETICS_ENTITY") || vehicle2.isLeashed()) {
                    return;
                }
                double maximumSpeed = vehicle2.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle2.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle2) * 0.5;
                }
                MotionState motionState = this.playerData.getMotionState();
                if (motionState.isSpeedEffect()) {
                    MotionState motionState2 = this.playerData.getMotionState();
                    maximumSpeed += motionState2.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed += 0.175F;
                }
                MotionState motionState3 = this.playerData.getMotionState();
                if (!motionState3.isSpeedEffect()) {
                    MotionState motionState4 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed += 0.5;
                }
                double horizontalSpeedDifference = horizontalSpeed - maximumSpeed;
                boolean aboveHorizontalSpeedDifferenceLimit = horizontalSpeedDifference > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifferenceLimit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed + " diff=" + horizontalSpeedDifference);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof Mule)) {
                Mule vehicle3 = (Mule) this.playerData.getPlayer().getVehicle();
                if (vehicle3 == null || vehicle3.isLeashed()) {
                    return;
                }
                double maximumSpeed2 = vehicle3.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle3.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed2 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle3) * 0.5;
                }
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.isSpeedEffect()) {
                    MotionState motionState8 = this.playerData.getMotionState();
                    maximumSpeed2 += motionState8.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed2 += 0.175F;
                }
                MotionState motionState9 = this.playerData.getMotionState();
                if (!motionState9.isSpeedEffect()) {
                    MotionState motionState10 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed2 += 0.5;
                }
                double horizontalSpeedDifference2 = horizontalSpeed - maximumSpeed2;
                boolean aboveHorizontalSpeedDifference2Limit = horizontalSpeedDifference2 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference2Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Mule] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed2 + " diff=" + horizontalSpeedDifference2);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof Donkey)) {
                Donkey vehicle4 = (Donkey) this.playerData.getPlayer().getVehicle();
                if (vehicle4 == null || vehicle4.isLeashed()) {
                    return;
                }
                double maximumSpeed3 = vehicle4.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle4.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed3 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle4) * 0.5;
                }
                MotionState motionState13 = this.playerData.getMotionState();
                if (motionState13.isSpeedEffect()) {
                    MotionState motionState14 = this.playerData.getMotionState();
                    maximumSpeed3 += motionState14.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed3 += 0.175F;
                }
                MotionState motionState15 = this.playerData.getMotionState();
                if (!motionState15.isSpeedEffect()) {
                    MotionState motionState16 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed3 += 0.5;
                }
                double horizontalSpeedDifference3 = horizontalSpeed - maximumSpeed3;
                boolean aboveHorizontalSpeedDifference3Limit = horizontalSpeedDifference3 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference3Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Donkey] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed3 + " diff=" + horizontalSpeedDifference3);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof ZombieHorse)) {
                ZombieHorse vehicle5 = (ZombieHorse) this.playerData.getPlayer().getVehicle();
                if (vehicle5 == null || vehicle5.isLeashed()) {
                    return;
                }
                double maximumSpeed4 = vehicle5.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle5.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed4 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle5) * 0.5;
                }
                MotionState motionState19 = this.playerData.getMotionState();
                if (motionState19.isSpeedEffect()) {
                    MotionState motionState20 = this.playerData.getMotionState();
                    maximumSpeed4 += motionState20.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed4 += 0.175F;
                }
                MotionState motionState21 = this.playerData.getMotionState();
                if (!motionState21.isSpeedEffect()) {
                    MotionState motionState22 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed4 += 0.5;
                }
                double horizontalSpeedDifference4 = horizontalSpeed - maximumSpeed4;
                boolean aboveHorizontalSpeedDifference4Limit = horizontalSpeedDifference4 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference4Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Zombie Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed4 + " diff=" + horizontalSpeedDifference4);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof SkeletonHorse)) {
                SkeletonHorse vehicle6 = (SkeletonHorse) this.playerData.getPlayer().getVehicle();
                if (vehicle6 == null || vehicle6.isLeashed()) {
                    return;
                }
                double maximumSpeed5 = vehicle6.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle6.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed5 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle6) * 0.15;
                }
                MotionState motionState25 = this.playerData.getMotionState();
                if (motionState25.isSpeedEffect()) {
                    MotionState motionState26 = this.playerData.getMotionState();
                    maximumSpeed5 += motionState26.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed5 += 0.175F;
                }
                MotionState motionState27 = this.playerData.getMotionState();
                if (!motionState27.isSpeedEffect()) {
                    MotionState motionState28 = this.playerData.getMotionState();
                }
                CollisionState collisionState3 = this.playerData.getCollisionState();
                if (collisionState3.getTicksSinceSurroundingLiquid() < 20) {
                    maximumSpeed5 += 0.3;
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed5 += 0.5;
                }
                double horizontalSpeedDifference5 = horizontalSpeed - maximumSpeed5;
                boolean aboveHorizontalSpeedDifference5Limit = horizontalSpeedDifference5 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference5Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Skeleton Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed5 + " diff=" + horizontalSpeedDifference5);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Boat)) {
                double max = 0.65;
                if (recentSurroundingLiquid) {
                    max += 0.15;
                }
                double horizontalSpeedDifference6 = horizontalSpeed - max;
                boolean aboveHorizontalSpeedDifference6Limit = horizontalSpeedDifference6 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference6Limit && !recentSurroundingIce && !recentSurroundingBubbleColumn && !recentSurroundingSlime && ticksInVehicle > 5) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Boat] deltaXZ=" + horizontalSpeed + " max=" + max + " diff=" + horizontalSpeedDifference6);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Pig)) {
                Pig vehicle7 = (Pig) this.playerData.getPlayer().getVehicle();
                if (vehicle7 == null || vehicle7.isLeashed()) {
                    return;
                }
                double maximumSpeed6 = vehicle7.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle7.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed6 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle7) * 0.5;
                }
                MotionState motionState31 = this.playerData.getMotionState();
                if (!motionState31.isSpeedEffect()) {
                    MotionState motionState32 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed6 += 0.5;
                }
                double horizontalSpeedDifference7 = horizontalSpeed - maximumSpeed6;
                boolean aboveHorizontalSpeedDifference7Limit = horizontalSpeedDifference7 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference7Limit && !recentSurroundingIce && ticksInVehicle > 10 && alignedVehicleY) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Pig] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed6 + " diff=" + horizontalSpeedDifference7);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_16()) && (this.playerData.getPlayer().getVehicle() instanceof Strider)) {
                Strider vehicle8 = (Strider) this.playerData.getPlayer().getVehicle();
                if (vehicle8 == null || vehicle8.isLeashed()) {
                    return;
                }
                double maximumSpeed7 = vehicle8.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle8.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed7 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle8) * 0.15;
                }
                MotionState motionState35 = this.playerData.getMotionState();
                if (motionState35.isSpeedEffect()) {
                    MotionState motionState36 = this.playerData.getMotionState();
                    maximumSpeed7 += motionState36.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed7 += 0.175F;
                }
                MotionState motionState37 = this.playerData.getMotionState();
                if (!motionState37.isSpeedEffect()) {
                    MotionState motionState38 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed7 += 0.5;
                }
                double horizontalSpeedDifference8 = horizontalSpeed - maximumSpeed7;
                boolean aboveHorizontalSpeedDifference8Limit = horizontalSpeedDifference8 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference8Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Strider] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed7 + " diff=" + horizontalSpeedDifference8);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof Llama)) {
                Llama vehicle9 = (Llama) this.playerData.getPlayer().getVehicle();
                if (vehicle9 == null || vehicle9.isLeashed()) {
                    return;
                }
                double maximumSpeed8 = vehicle9.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle9.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed8 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle9) * 0.45;
                }
                MotionState motionState41 = this.playerData.getMotionState();
                if (motionState41.isSpeedEffect()) {
                    MotionState motionState42 = this.playerData.getMotionState();
                    maximumSpeed8 += motionState42.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed8 += 0.175F;
                }
                MotionState motionState43 = this.playerData.getMotionState();
                if (!motionState43.isSpeedEffect()) {
                    MotionState motionState44 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed8 += 0.5;
                }
                double horizontalSpeedDifference9 = horizontalSpeed - maximumSpeed8;
                boolean aboveHorizontalSpeedDifference9Limit = horizontalSpeedDifference9 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference9Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Llama] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed8 + " diff=" + horizontalSpeedDifference9);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof TraderLlama)) {
                TraderLlama vehicle10 = (TraderLlama) this.playerData.getPlayer().getVehicle();
                if (vehicle10 == null || vehicle10.isLeashed()) {
                    return;
                }
                double maximumSpeed9 = vehicle10.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle10.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed9 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle10) * 0.15;
                }
                MotionState motionState47 = this.playerData.getMotionState();
                if (motionState47.isSpeedEffect()) {
                    MotionState motionState48 = this.playerData.getMotionState();
                    maximumSpeed9 += motionState48.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed9 += 0.175F;
                }
                MotionState motionState49 = this.playerData.getMotionState();
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed9 += 0.5;
                }
                double horizontalSpeedDifference10 = horizontalSpeed - maximumSpeed9;
                boolean aboveHorizontalSpeedDifference10Limit = horizontalSpeedDifference10 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference10Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[TraderLlama] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed9 + " diff=" + horizontalSpeedDifference10);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof Camel)) {
                Camel vehicle11 = (Camel) this.playerData.getPlayer().getVehicle();
                if (vehicle11 == null || vehicle11.isLeashed()) {
                    return;
                }
                double maximumSpeed10 = vehicle11.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.45;
                if (vehicle11.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed10 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle11) * 0.45;
                }
                MotionState motionState52 = this.playerData.getMotionState();
                if (motionState52.isSpeedEffect()) {
                    MotionState motionState53 = this.playerData.getMotionState();
                    maximumSpeed10 += motionState53.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed10 += 0.175F;
                }
                MotionState motionState54 = this.playerData.getMotionState();
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed10 += 0.5;
                }
                if (vehicle11.isDashing()) {
                    maximumSpeed10 += 0.25;
                }
                double horizontalSpeedDifference11 = horizontalSpeed - maximumSpeed10;
                boolean aboveHorizontalSpeedDifference11Limit = horizontalSpeedDifference11 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference11Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Camel] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed10 + " diff=" + horizontalSpeedDifference11);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof PlayerMoveEvent) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            if (this.playerData.getPlayer() == null) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            boolean recentSurroundingLiquid = collisionState.getTicksSinceSurroundingLiquid() < 100;
            boolean recentSurroundingSlime = this.playerData.getCollisionState().getTicksSinceSurroundingSlime() < 100;
            boolean recentSurroundingBubbleColumn = this.playerData.getCollisionState().getTicksSinceSurroundingBubbleColumn() < 100;
            boolean recentSurroundingIce = this.playerData.getCollisionState().getTicksSinceSurroundingIce() < 100;
            boolean recentSurroundingPiston = this.playerData.getCollisionState().getTicksSinceSurroundingPiston() < 40;
            if (recentSurroundingPiston) {
                return;
            }
            int ticksInVehicle = this.playerData.getCollisionState().getTicksInVehicle();
            double horizontalSpeed = this.playerData.getCollisionState().getVehicleHorizontalSpeed();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean alignedVehicleY = collisionState2.getVehicleY() % 0.015625 == 0.0;
            if (this.playerData.getPlayer().getVehicle() instanceof LivingEntity) {
                LivingEntity vehicle = (LivingEntity) this.playerData.getPlayer().getVehicle();
                if (CheckUtils.getSpeedPotionLevel(vehicle) > 0) {
                    this.ticksSinceVehicleSpeedEffect = 0;
                } else {
                    this.ticksSinceVehicleSpeedEffect++;
                }
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Horse)) {
                Horse vehicle2 = (Horse) this.playerData.getPlayer().getVehicle();
                if (vehicle2 == null || vehicle2.hasMetadata("PROCOSMETICS_ENTITY") || vehicle2.isLeashed()) {
                    return;
                }
                double maximumSpeed = vehicle2.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle2.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle2) * 0.5;
                }
                MotionState motionState = this.playerData.getMotionState();
                if (motionState.isSpeedEffect()) {
                    MotionState motionState2 = this.playerData.getMotionState();
                    maximumSpeed += motionState2.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed += 0.175F;
                }
                MotionState motionState3 = this.playerData.getMotionState();
                if (!motionState3.isSpeedEffect()) {
                    MotionState motionState4 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed += 0.5;
                }
                double horizontalSpeedDifference = horizontalSpeed - maximumSpeed;
                boolean aboveHorizontalSpeedDifferenceLimit = horizontalSpeedDifference > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifferenceLimit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed + " diff=" + horizontalSpeedDifference);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof Mule)) {
                Mule vehicle3 = (Mule) this.playerData.getPlayer().getVehicle();
                if (vehicle3 == null || vehicle3.isLeashed()) {
                    return;
                }
                double maximumSpeed2 = vehicle3.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle3.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed2 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle3) * 0.5;
                }
                MotionState motionState7 = this.playerData.getMotionState();
                if (motionState7.isSpeedEffect()) {
                    MotionState motionState8 = this.playerData.getMotionState();
                    maximumSpeed2 += motionState8.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed2 += 0.175F;
                }
                MotionState motionState9 = this.playerData.getMotionState();
                if (!motionState9.isSpeedEffect()) {
                    MotionState motionState10 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed2 += 0.5;
                }
                double horizontalSpeedDifference2 = horizontalSpeed - maximumSpeed2;
                boolean aboveHorizontalSpeedDifference2Limit = horizontalSpeedDifference2 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference2Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Mule] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed2 + " diff=" + horizontalSpeedDifference2);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof Donkey)) {
                Donkey vehicle4 = (Donkey) this.playerData.getPlayer().getVehicle();
                if (vehicle4 == null || vehicle4.isLeashed()) {
                    return;
                }
                double maximumSpeed3 = vehicle4.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle4.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed3 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle4) * 0.5;
                }
                MotionState motionState13 = this.playerData.getMotionState();
                if (motionState13.isSpeedEffect()) {
                    MotionState motionState14 = this.playerData.getMotionState();
                    maximumSpeed3 += motionState14.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed3 += 0.175F;
                }
                MotionState motionState15 = this.playerData.getMotionState();
                if (!motionState15.isSpeedEffect()) {
                    MotionState motionState16 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed3 += 0.5;
                }
                double horizontalSpeedDifference3 = horizontalSpeed - maximumSpeed3;
                boolean aboveHorizontalSpeedDifference3Limit = horizontalSpeedDifference3 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference3Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Donkey] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed3 + " diff=" + horizontalSpeedDifference3);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof ZombieHorse)) {
                ZombieHorse vehicle5 = (ZombieHorse) this.playerData.getPlayer().getVehicle();
                if (vehicle5 == null || vehicle5.isLeashed()) {
                    return;
                }
                double maximumSpeed4 = vehicle5.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle5.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed4 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle5) * 0.5;
                }
                MotionState motionState19 = this.playerData.getMotionState();
                if (motionState19.isSpeedEffect()) {
                    MotionState motionState20 = this.playerData.getMotionState();
                    maximumSpeed4 += motionState20.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed4 += 0.175F;
                }
                MotionState motionState21 = this.playerData.getMotionState();
                if (!motionState21.isSpeedEffect()) {
                    MotionState motionState22 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed4 += 0.5;
                }
                double horizontalSpeedDifference4 = horizontalSpeed - maximumSpeed4;
                boolean aboveHorizontalSpeedDifference4Limit = horizontalSpeedDifference4 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference4Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Zombie Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed4 + " diff=" + horizontalSpeedDifference4);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_12()) && (this.playerData.getPlayer().getVehicle() instanceof SkeletonHorse)) {
                SkeletonHorse vehicle6 = (SkeletonHorse) this.playerData.getPlayer().getVehicle();
                if (vehicle6 == null || vehicle6.isLeashed()) {
                    return;
                }
                double maximumSpeed5 = vehicle6.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle6.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed5 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle6) * 0.15;
                }
                MotionState motionState25 = this.playerData.getMotionState();
                if (motionState25.isSpeedEffect()) {
                    MotionState motionState26 = this.playerData.getMotionState();
                    maximumSpeed5 += motionState26.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed5 += 0.175F;
                }
                MotionState motionState27 = this.playerData.getMotionState();
                if (!motionState27.isSpeedEffect()) {
                    MotionState motionState28 = this.playerData.getMotionState();
                }
                CollisionState collisionState3 = this.playerData.getCollisionState();
                if (collisionState3.getTicksSinceSurroundingLiquid() < 20) {
                    maximumSpeed5 += 0.3;
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed5 += 0.5;
                }
                double horizontalSpeedDifference5 = horizontalSpeed - maximumSpeed5;
                boolean aboveHorizontalSpeedDifference5Limit = horizontalSpeedDifference5 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference5Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Skeleton Horse] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed5 + " diff=" + horizontalSpeedDifference5);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Boat)) {
                double max = 0.65;
                if (recentSurroundingLiquid) {
                    max += 0.15;
                }
                double horizontalSpeedDifference6 = horizontalSpeed - max;
                boolean aboveHorizontalSpeedDifference6Limit = horizontalSpeedDifference6 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference6Limit && !recentSurroundingIce && !recentSurroundingBubbleColumn && !recentSurroundingSlime && ticksInVehicle > 5) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Boat] deltaXZ=" + horizontalSpeed + " max=" + max + " diff=" + horizontalSpeedDifference6);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((this.playerData.getPlayer().getVehicle() != null) && (this.playerData.getPlayer().getVehicle() instanceof Pig)) {
                Pig vehicle7 = (Pig) this.playerData.getPlayer().getVehicle();
                if (vehicle7 == null || vehicle7.isLeashed()) {
                    return;
                }
                double maximumSpeed6 = vehicle7.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle7.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed6 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle7) * 0.5;
                }
                MotionState motionState31 = this.playerData.getMotionState();
                if (!motionState31.isSpeedEffect()) {
                    MotionState motionState32 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed6 += 0.5;
                }
                double horizontalSpeedDifference7 = horizontalSpeed - maximumSpeed6;
                boolean aboveHorizontalSpeedDifference7Limit = horizontalSpeedDifference7 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference7Limit && !recentSurroundingIce && ticksInVehicle > 10 && alignedVehicleY) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Pig] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed6 + " diff=" + horizontalSpeedDifference7);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_16()) && (this.playerData.getPlayer().getVehicle() instanceof Strider)) {
                Strider vehicle8 = (Strider) this.playerData.getPlayer().getVehicle();
                if (vehicle8 == null || vehicle8.isLeashed()) {
                    return;
                }
                double maximumSpeed7 = vehicle8.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle8.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed7 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle8) * 0.15;
                }
                MotionState motionState35 = this.playerData.getMotionState();
                if (motionState35.isSpeedEffect()) {
                    MotionState motionState36 = this.playerData.getMotionState();
                    maximumSpeed7 += motionState36.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed7 += 0.175F;
                }
                MotionState motionState37 = this.playerData.getMotionState();
                if (!motionState37.isSpeedEffect()) {
                    MotionState motionState38 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed7 += 0.5;
                }
                double horizontalSpeedDifference8 = horizontalSpeed - maximumSpeed7;
                boolean aboveHorizontalSpeedDifference8Limit = horizontalSpeedDifference8 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference8Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Strider] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed7 + " diff=" + horizontalSpeedDifference8);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof Llama)) {
                Llama vehicle9 = (Llama) this.playerData.getPlayer().getVehicle();
                if (vehicle9 == null || vehicle9.isLeashed()) {
                    return;
                }
                double maximumSpeed8 = vehicle9.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle9.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed8 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle9) * 0.45;
                }
                MotionState motionState41 = this.playerData.getMotionState();
                if (motionState41.isSpeedEffect()) {
                    MotionState motionState42 = this.playerData.getMotionState();
                    maximumSpeed8 += motionState42.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed8 += 0.175F;
                }
                MotionState motionState43 = this.playerData.getMotionState();
                if (!motionState43.isSpeedEffect()) {
                    MotionState motionState44 = this.playerData.getMotionState();
                }
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed8 += 0.5;
                }
                double horizontalSpeedDifference9 = horizontalSpeed - maximumSpeed8;
                boolean aboveHorizontalSpeedDifference9Limit = horizontalSpeedDifference9 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference9Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Llama] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed8 + " diff=" + horizontalSpeedDifference9);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof TraderLlama)) {
                TraderLlama vehicle10 = (TraderLlama) this.playerData.getPlayer().getVehicle();
                if (vehicle10 == null || vehicle10.isLeashed()) {
                    return;
                }
                double maximumSpeed9 = vehicle10.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.1;
                if (vehicle10.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed9 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle10) * 0.15;
                }
                MotionState motionState47 = this.playerData.getMotionState();
                if (motionState47.isSpeedEffect()) {
                    MotionState motionState48 = this.playerData.getMotionState();
                    maximumSpeed9 += motionState48.getSpeedAmplifier() * 0.065;
                }
                if (!alignedVehicleY) {
                    maximumSpeed9 += 0.175F;
                }
                MotionState motionState49 = this.playerData.getMotionState();
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed9 += 0.5;
                }
                double horizontalSpeedDifference10 = horizontalSpeed - maximumSpeed9;
                boolean aboveHorizontalSpeedDifference10Limit = horizontalSpeedDifference10 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference10Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[TraderLlama] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed9 + " diff=" + horizontalSpeedDifference10);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                        return;
                    }
                } else {
                    this.decayBuffer();
                }
                return;
            }
            if ((ExemptionManager.isAtLeast1_11()) && (this.playerData.getPlayer().getVehicle() instanceof Camel)) {
                Camel vehicle11 = (Camel) this.playerData.getPlayer().getVehicle();
                if (vehicle11 == null || vehicle11.isLeashed()) {
                    return;
                }
                double maximumSpeed10 = vehicle11.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 2.25 + 0.45;
                if (vehicle11.hasPotionEffect(PotionEffectType.SPEED)) {
                    maximumSpeed10 += CheckUtils.getSpeedPotionLevel((LivingEntity) vehicle11) * 0.45;
                }
                MotionState motionState52 = this.playerData.getMotionState();
                if (motionState52.isSpeedEffect()) {
                    MotionState motionState53 = this.playerData.getMotionState();
                    maximumSpeed10 += motionState53.getSpeedAmplifier() * 0.085;
                }
                if (!alignedVehicleY) {
                    maximumSpeed10 += 0.175F;
                }
                MotionState motionState54 = this.playerData.getMotionState();
                if (this.ticksSinceVehicleSpeedEffect < 20 && this.ticksSinceVehicleSpeedEffect > 0) {
                    maximumSpeed10 += 0.5;
                }
                if (vehicle11.isDashing()) {
                    maximumSpeed10 += 0.25;
                }
                double horizontalSpeedDifference11 = horizontalSpeed - maximumSpeed10;
                boolean aboveHorizontalSpeedDifference11Limit = horizontalSpeedDifference11 > VulcanSettings.checksMovementEntityspeedAMinDifference;
                if (aboveHorizontalSpeedDifference11Limit && !recentSurroundingIce && ticksInVehicle > 10) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("[Camel] speed=" + horizontalSpeed + " maxSpeed=" + maximumSpeed10 + " diff=" + horizontalSpeedDifference11);
                        if (VulcanSettings.checksMovementEntityspeedAKickOut) {
                            MainThreadExecutor.execute(this::ejectPlayer);
                        }
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    private void ejectPlayer() {
        this.playerData.getPlayer().leaveVehicle();
    }
}
