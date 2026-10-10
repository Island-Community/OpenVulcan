package me.frep.vulcan.spigot.check.impl.movement.boatfly;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Boat;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Boat Fly", type = 'A', complexType = "Vertical", description = "Moving upwards in a boat.")
public class BoatFlyA extends AbstractCheck {

    public BoatFlyA(PlayerData playerData) {
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
            double vehicleDeltaY = this.playerData.getCollisionState().getVehicleDeltaY();
            CollisionState collisionState = this.playerData.getCollisionState();
            boolean recentSurroundingLiquid = collisionState.getTicksSinceSurroundingLiquid() < 100;
            boolean recentSurroundingSlime = this.playerData.getCollisionState().getTicksSinceSurroundingSlime() < 100;
            boolean recentSurroundingBubbleColumn = this.playerData.getCollisionState().getTicksSinceSurroundingBubbleColumn() < 100;
            boolean recentSurroundingPiston = this.playerData.getCollisionState().getTicksSinceSurroundingPiston() < 50;
            boolean recentSurroundingBed = this.playerData.getCollisionState().getTicksSinceSurroundingBed() < 40;
            boolean belowUnloadedChunkTicksLimit = this.playerData.getCollisionState().getUnloadedChunkTicks() < 60;
            boolean surroundedByAir = this.playerData.getCollisionState().isSurroundedByAir();
            if (this.playerData.getCollisionState().hasLivingEntityNearby()) {
                return;
            }
            if (ExemptionManager.isNewerThan1_13() && !vehicle.hasGravity()) {
                return;
            }
            if (PluginServices.INSTANCE.getHookedBoatTicks().containsKey(vehicle.getEntityId())) {
                return;
            }
            boolean condition7 = vehicleDeltaY > 0.01 && !recentSurroundingSlime && !recentSurroundingLiquid && !recentSurroundingBubbleColumn && !recentSurroundingPiston && !recentSurroundingBed && !belowUnloadedChunkTicksLimit && surroundedByAir;
            if (condition7) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaY=" + vehicleDeltaY);
                    if (VulcanSettings.checksMovementBoatflyAKickOut) {
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
