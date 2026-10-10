package me.frep.vulcan.spigot.check.impl.movement.boatfly;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Boat;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Boat Fly", type = 'B', complexType = "Vertical", description = "Moving too quickly horizontally.")
public class BoatFlyB extends AbstractCheck {

    public BoatFlyB(PlayerData playerData) {
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
        if ((!ExemptionManager.isBefore1_9()) && (this.playerData.getPlayer() != null)) {
            if (!(this.playerData.getPlayer().getVehicle() instanceof Boat)) {
                return;
            }
            int ticksInVehicle = this.playerData.getCollisionState().getTicksInVehicle();
            if (ticksInVehicle <= 5) {
                return;
            }
            Boat vehicle = (Boat) this.playerData.getPlayer().getVehicle();
            double horizontalSpeed = this.playerData.getCollisionState().getVehicleHorizontalSpeed();
            double vehicleDeltaY = this.playerData.getCollisionState().getVehicleDeltaY();
            boolean recentSurroundingIce = this.playerData.getCollisionState().getTicksSinceSurroundingIce() < 100;
            CollisionState collisionState = this.playerData.getCollisionState();
            boolean recentSurroundingLiquid = collisionState.getTicksSinceSurroundingLiquid() < 100;
            boolean recentSurroundingSlime = this.playerData.getCollisionState().getTicksSinceSurroundingSlime() < 100;
            boolean recentSurroundingBubbleColumn = this.playerData.getCollisionState().getTicksSinceSurroundingBubbleColumn() < 100;
            boolean recentSurroundingPiston = this.playerData.getCollisionState().getTicksSinceSurroundingPiston() < 100;
            int nearbyBoatCount = this.playerData.getCollisionState().getNearbyBoatCount();
            if (ExemptionManager.isNewerThan1_13() && !vehicle.hasGravity()) {
                return;
            }
            if (PluginServices.INSTANCE.getHookedBoatTicks().containsKey(vehicle.getEntityId())) {
                return;
            }
            if (this.playerData.getCollisionState().hasLivingEntityNearby()) {
                return;
            }
            double amount = 0.25;
            if (this.isExempt(new ExemptType[] { ExemptType.WATERLOGGED })) {
                amount += 0.2;
            }
            boolean condition6 = horizontalSpeed > amount && vehicleDeltaY > -0.05 && !recentSurroundingLiquid && !recentSurroundingSlime && !recentSurroundingBubbleColumn && !recentSurroundingIce && !recentSurroundingPiston;
            if (condition6 && nearbyBoatCount == 1) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaXZ=" + horizontalSpeed + " deltaY=" + vehicleDeltaY);
                    if (VulcanSettings.checksMovementBoatflyBKickOut) {
                        MainThreadExecutor.execute(this::ejectPlayer);
                    }
                }
            } else {
                this.decayBuffer();
            }
            return;
        }
    }

    private void ejectPlayer() {
        this.playerData.getPlayer().leaveVehicle();
    }
}
