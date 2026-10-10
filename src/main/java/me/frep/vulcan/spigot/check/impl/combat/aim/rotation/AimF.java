package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aim", type = 'F', complexType = "Straight", description = "Invalid yaw change.")
public class AimF extends AbstractCheck {

    public AimF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(3))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float deltaYaw = rotationTracker.getDeltaYaw();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            float deltaPitch = rotationTracker2.getDeltaPitch();
            RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
            float pitch = rotationTracker3.getPitch();
            RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
            double attackDistance = relativeMoveTracker.getAttackDistance();
            RelativeMoveTracker relativeMoveTracker2 = this.playerData.getRelativeMoveTracker();
            Entity entity = relativeMoveTracker2.getLastAttackedEntity();
            if (entity == null || !(entity instanceof Player player)) {
                return;
            }
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData = playerDataStore.getPlayerData(player);
            if (playerData == null) {
                return;
            }
            boolean exempt;
            PlayerToggles playerToggles = playerData.getPlayerToggles();
            exempt = playerToggles.isExempt(ExemptType.VEHICLE);
            condition = (deltaPitch == 0.0F && deltaYaw > 1.825F && Math.abs(pitch) < 60.0F && attackDistance > 1.0) && ((playerData.getCollisionState()).getDeltaXZ() > 0.03);
            boolean condition2 = condition;
            if (condition2 && !exempt) {
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
