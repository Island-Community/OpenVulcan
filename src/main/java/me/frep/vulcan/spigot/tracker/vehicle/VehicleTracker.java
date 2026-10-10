package me.frep.vulcan.spigot.tracker.vehicle;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.entity.TrackedEntity;

public class VehicleTracker {

    private final PlayerData playerData;

    private boolean riding = false;

    private EntityType entityType = null;

    private int vehicleEntityId = -1;

    public VehicleTracker(PlayerData data) {
        this.playerData = data;
    }

    public void handlePassengers(WrapperPlayServerSetPassengers packet) {
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyPassengers(packet));
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public int getVehicleEntityId() {
        return this.vehicleEntityId;
    }

    private void applyPassengers(WrapperPlayServerSetPassengers packet) {
        riding = false;
        for (int passenger : packet.getPassengers()) {
            if (passenger == playerData.getEntityId()) {
                riding = true;
                break;
            }
        }
        vehicleEntityId = riding ? packet.getEntityId() : -1;
        TrackedEntity vehicle = riding ? playerData.getEntityTracker().getTrackedEntity(vehicleEntityId) : null;
        entityType = vehicle == null ? null : vehicle.getEntityType();
    }
}
