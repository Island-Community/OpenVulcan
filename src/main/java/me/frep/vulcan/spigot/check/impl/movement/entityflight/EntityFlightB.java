package me.frep.vulcan.spigot.check.impl.movement.entityflight;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Entity Flight", type = 'B', complexType = "Hover", description = "Hovering while riding an entity.")
public class EntityFlightB extends AbstractCheck {

    public EntityFlightB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isVehicleMovePacket(packetEvent)) {
            if (PluginServices.INSTANCE.isFoliaServer()) {
                return;
            }
            this.checkVehicleMovement();
        }
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof PlayerMoveEvent) {
            this.checkVehicleMovement();
        }
    }

    private void checkVehicleMovement() {
        if ((!ExemptionManager.isBefore1_9()) && (this.playerData.getPlayer() != null) && (!this.playerData.getPlayer().isDead())) {
            CollisionState collisionState = this.playerData.getCollisionState();
            if (collisionState.getVehicleY() < 0.0) {
                return;
            }
            Entity entity = this.playerData.getPlayer().getVehicle();
            if (entity == null) {
                return;
            }
            String text;
            if (entity instanceof Horse) {
                text = "Horse";
            } else if (entity instanceof Pig) {
                text = "Pig";
            } else if (ExemptionManager.isAtLeast1_9() && entity instanceof Donkey) {
                text = "Donkey";
            } else if (entity instanceof ZombieHorse) {
                text = "Zombie Horse";
            } else if (entity instanceof SkeletonHorse) {
                text = "Skeleton Horse";
            } else {
                if (!ExemptionManager.isAtLeast1_20() || !(entity instanceof Camel)) {
                    return;
                }
                text = "Camel";
            }
            LivingEntity entity2 = (LivingEntity) entity;
            if (ExemptionManager.isNewerThan1_13()) {
                if (entity2.hasPotionEffect(PotionEffectType.LEVITATION)) {
                    return;
                }
                if (entity2.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
                    return;
                }
            }
            if (entity2.isLeashed()) {
                return;
            }
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean recentSurroundingLiquid = collisionState2.getTicksSinceSurroundingLiquid() < 100;
            boolean recentSurroundingSlime = this.playerData.getCollisionState().getTicksSinceSurroundingSlime() < 100;
            boolean recentSurroundingBubbleColumn = this.playerData.getCollisionState().getTicksSinceSurroundingBubbleColumn() < 100;
            int ticksInVehicle = this.playerData.getCollisionState().getTicksInVehicle();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            int ticksInOpenAir = collisionState3.getTicksInOpenAir();
            double vehicleDeltaY = this.playerData.getCollisionState().getVehicleDeltaY();
            double previousVehicleDeltaY = this.playerData.getCollisionState().getPreviousVehicleDeltaY();
            double vehicleDeltaYDifference = Math.abs(vehicleDeltaY - previousVehicleDeltaY);
            boolean condition4 = ticksInOpenAir > 30 && Math.abs(vehicleDeltaY) < 5.0 && vehicleDeltaYDifference < 0.05 && !recentSurroundingLiquid && !recentSurroundingSlime && !recentSurroundingBubbleColumn && ticksInVehicle > 5 && vehicleDeltaY > -1.15;
            if (condition4) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("[" + text + "] deltaY=" + vehicleDeltaY + " acceleration=" + vehicleDeltaYDifference + " ticks=" + ticksInVehicle);
                }
            } else {
                this.decayBuffer();
            }
            return;
        }
    }
}
