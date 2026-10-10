package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aim", type = 'N', complexType = "Small Yaw", experimental = false, description = "Too small yaw change.")
public class AimN extends AbstractCheck {

    public AimN(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(3))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker2.getDeltaPitch();
            boolean condition = deltaYaw < 0.05 && deltaYaw > 0.0F && deltaPitch == 0.0F;
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
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.VEHICLE, ExemptType.BOAT });
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaYaw=" + deltaYaw + " deltaPitch=" + deltaPitch);
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
