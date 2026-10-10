package me.frep.vulcan.spigot.tracker.entity;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.EntityPositionData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.frep.vulcan.spigot.player.session.PlayerData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

public class EntityTracker {

    private final PlayerData playerData;

    private final Map<Integer, TrackedEntity> trackedEntities = new HashMap<>();

    public EntityTracker(PlayerData data) {
        this.playerData = data;
    }

    public void handleEvent(PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            WrapperPlayServerSpawnEntity wrapperPlayServerSpawnEntity = new WrapperPlayServerSpawnEntity(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.trackSpawnEntity(wrapperPlayServerSpawnEntity));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            WrapperPlayServerSpawnLivingEntity wrapperPlayServerSpawnLivingEntity = new WrapperPlayServerSpawnLivingEntity(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.trackSpawnLivingEntity(wrapperPlayServerSpawnLivingEntity));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            WrapperPlayServerSpawnPlayer wrapperPlayServerSpawnPlayer = new WrapperPlayServerSpawnPlayer(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.trackSpawnPlayer(wrapperPlayServerSpawnPlayer));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities wrapperPlayServerDestroyEntities = new WrapperPlayServerDestroyEntities(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.removeDestroyedEntities(wrapperPlayServerDestroyEntities));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove wrapperPlayServerEntityRelativeMove = new WrapperPlayServerEntityRelativeMove(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyRelativePosition(wrapperPlayServerEntityRelativeMove));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation wrapperPlayServerEntityRelativeMoveAndRotation = new WrapperPlayServerEntityRelativeMoveAndRotation(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyRelativePositionAndRotation(wrapperPlayServerEntityRelativeMoveAndRotation));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport wrapperPlayServerEntityTeleport = new WrapperPlayServerEntityTeleport(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyEntityTeleport(wrapperPlayServerEntityTeleport));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            WrapperPlayServerEntityRotation wrapperPlayServerEntityRotation = new WrapperPlayServerEntityRotation(packetSendEvent);
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyEntityRotation(wrapperPlayServerEntityRotation));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
            WrapperPlayServerEntityPositionSync wrapperPlayServerEntityPositionSync = new WrapperPlayServerEntityPositionSync(packetSendEvent);
            EntityPositionData entityPositionData = wrapperPlayServerEntityPositionSync.getValues();
            Vector3d vector3d = entityPositionData.getPosition();
            this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyEntityPositionSync(wrapperPlayServerEntityPositionSync, vector3d, entityPositionData));
        } else if (packetSendEvent.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment wrapperPlayServerEntityEquipment = new WrapperPlayServerEntityEquipment(packetSendEvent);
            TrackedEntity trackedEntity = this.trackedEntities.get(wrapperPlayServerEntityEquipment.getEntityId());
            if (trackedEntity == null) {
                return;
            }
            for (Equipment equipment : wrapperPlayServerEntityEquipment.getEquipment()) {
                trackedEntity.setEquipment(equipment.getSlot(), equipment.getItem());
            }
        }
    }

    private void trackEntity(int entityId, UUID uniqueId, EntityType entityType, Vector3d position) {
        TrackedEntity entity = new TrackedEntity(entityId);
        entity.setUniqueId(uniqueId);
        entity.setEntityType(entityType);
        entity.setX(position.getX());
        entity.setY(position.getY());
        entity.setZ(position.getZ());
        this.trackedEntities.put(entityId, entity);
    }

    private void updateEntity(int id, double x, double y, double z, boolean relative, Float yaw, Float pitch) {
        TrackedEntity entity = trackedEntities.get(id);
        if (entity == null)
            return;
        entity.setX(relative ? entity.getX() + x : x);
        entity.setY(relative ? entity.getY() + y : y);
        entity.setZ(relative ? entity.getZ() + z : z);
        if (yaw != null && pitch != null) {
            entity.setYaw(yaw);
            entity.setPitch(pitch);
        }
    }

    public TrackedEntity getTrackedEntity(int entityId) {
        return this.trackedEntities.get(entityId);
    }

    public List<TrackedEntity> getNearbyEntities(double x, double y, double z, double radius) {
        double radiusSquared = radius * radius;
        return this.trackedEntities.values().stream().filter(entity -> EntityTracker.isWithinRadius(x, y, z, radiusSquared, entity)).collect(Collectors.toList());
    }

    public List<TrackedEntity> getNearbyEntities(double radius) {
        double x = this.playerData.getCollisionState().getX();
        double y = this.playerData.getCollisionState().getY();
        double z = this.playerData.getCollisionState().getZ();
        double minX = x - radius;
        double maxX = x + radius;
        double minY = y - radius;
        double maxY = y + 1.8 + radius;
        double minZ = z - radius;
        double maxZ = z + radius;
        return this.trackedEntities.values().stream().filter(entity -> EntityTracker.isWithinBounds(minX, maxX, minY, maxY, minZ, maxZ, entity)).collect(Collectors.toList());
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public Map<Integer, TrackedEntity> getTrackedEntities() {
        return this.trackedEntities;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("EntityTracker(data=");
        return builder.append(this.getPlayerData()).append(", trackedEntities=").append(this.getTrackedEntities()).append(")").toString();
    }

    private static boolean isWithinBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, TrackedEntity entity) {
        return entity.getX() >= minX && entity.getX() <= maxX && entity.getY() >= minY && entity.getY() <= maxY && entity.getZ() >= minZ && entity.getZ() <= maxZ;
    }

    private static boolean isWithinRadius(double x, double y, double z, double radiusSquared, TrackedEntity entity) {
        double dx = entity.getX() - x;
        double dy = entity.getY() - y;
        double dz = entity.getZ() - z;
        return dx * dx + dy * dy + dz * dz <= radiusSquared;
    }

    private void applyEntityPositionSync(WrapperPlayServerEntityPositionSync packet, Vector3d position, EntityPositionData state) {
        updateEntity(packet.getId(), position.getX(), position.getY(), position.getZ(), false, state.getYaw(), state.getPitch());
    }

    private void applyEntityRotation(WrapperPlayServerEntityRotation packet) {
        TrackedEntity entity = trackedEntities.get(packet.getEntityId());
        if (entity != null) {
            entity.setYaw(packet.getYaw() * 0.7111111F);
            entity.setPitch(packet.getPitch() * 0.7111111F);
        }
    }

    private void applyEntityTeleport(WrapperPlayServerEntityTeleport packet) {
        updateEntity(packet.getEntityId(), packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), false, packet.getYaw(), packet.getPitch());
    }

    private void applyRelativePositionAndRotation(WrapperPlayServerEntityRelativeMoveAndRotation packet) {
        updateEntity(packet.getEntityId(), packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ(), true, packet.getYaw() * 0.7111111F, packet.getPitch() * 0.7111111F);
    }

    private void applyRelativePosition(WrapperPlayServerEntityRelativeMove packet) {
        updateEntity(packet.getEntityId(), packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ(), true, null, null);
    }

    private void removeDestroyedEntities(WrapperPlayServerDestroyEntities packet) {
        Arrays.stream(packet.getEntityIds()).forEach(this.trackedEntities::remove);
    }

    private void trackSpawnPlayer(WrapperPlayServerSpawnPlayer packet) {
        this.trackEntity(packet.getEntityId(), packet.getUUID(), EntityTypes.PLAYER, packet.getPosition());
    }

    private void trackSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        this.trackEntity(packet.getEntityId(), packet.getEntityUUID(), packet.getEntityType(), packet.getPosition());
    }

    private void trackSpawnEntity(WrapperPlayServerSpawnEntity packet) {
        this.trackEntity(packet.getEntityId(), packet.getUUID().orElse(null), packet.getEntityType(), packet.getPosition());
    }
}
