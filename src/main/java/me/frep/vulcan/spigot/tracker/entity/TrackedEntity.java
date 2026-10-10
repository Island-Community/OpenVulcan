package me.frep.vulcan.spigot.tracker.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import java.util.EnumMap;
import java.util.UUID;
import me.frep.vulcan.spigot.geometry.box.AxisAlignedBox;
import me.frep.vulcan.spigot.geometry.box.EntitySize;

public class TrackedEntity {

    private EntityType entityType;

    private UUID uniqueId;

    private float yaw;

    private float pitch;

    private double x;

    private double y;

    private double z;

    private final int entityId;

    private EnumMap<EquipmentSlot, ItemStack> equipment = null;

    public TrackedEntity(int entityId) {
        this.entityId = entityId;
    }

    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        if ((item == ItemStack.EMPTY) && (this.getEquipment(slot) == ItemStack.EMPTY)) {
            return;
        }
        if (this.equipment == null) {
            this.equipment = new EnumMap(EquipmentSlot.class);
        }
        this.equipment.put(slot, item);
    }

    public ItemStack getEquipment(EquipmentSlot equipmentSlot) {
        EquipmentSlot slot = equipmentSlot;
        if (this.equipment == null) {
            return ItemStack.EMPTY;
        }
        return this.equipment.getOrDefault(slot, ItemStack.EMPTY);
    }

    public boolean hasEquipment(EquipmentSlot slot) {
        if (this.equipment == null) {
            return false;
        }
        ItemStack item = this.equipment.get(slot);
        return item != null && !item.isEmpty();
    }

    public AxisAlignedBox getAxisAlignedBox() {
        return EntitySize.getEntitySize(this.entityType).getAxisAlignedBox(this.x, this.y, this.z);
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public EnumMap getEquipment() {
        return this.equipment;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setEquipment(EnumMap equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("TrackedEntity(entityType=");
        builder = builder.append(this.getEntityType()).append(", uuid=").append(this.getUniqueId()).append(", yaw=");
        builder = builder.append(this.getYaw()).append(", pitch=");
        builder = builder.append(this.getPitch()).append(", x=");
        builder = builder.append(this.getX()).append(", y=");
        builder = builder.append(this.getY()).append(", z=");
        builder = builder.append(this.getZ()).append(", entityId=");
        builder = builder.append(this.getEntityId()).append(", equipment=");
        return builder.append(this.getEquipment()).append(")").toString();
    }
}
