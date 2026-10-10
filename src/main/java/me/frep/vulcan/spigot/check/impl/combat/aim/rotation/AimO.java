package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aim", type = 'O', complexType = "Small Yaw", description = "Too small pitch change.")
public class AimO extends AbstractCheck {

    public AimO(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(2))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker2.getDeltaPitch();
            boolean condition = deltaPitch < 0.05 && deltaPitch > 0.0F && deltaYaw == 0.0F;
            RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
            Entity entity = relativeMoveTracker.getLastAttackedEntity();
            if (entity == null || !(entity instanceof Player player)) {
                return;
            }
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData = playerDataStore.getPlayerData(player);
            if (playerData == null) {
                return;
            }
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getDeltaXZ() < 0.03) {
                return;
            }
            if (condition) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaPitch=" + deltaPitch + " deltaYaw=" + deltaYaw);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
