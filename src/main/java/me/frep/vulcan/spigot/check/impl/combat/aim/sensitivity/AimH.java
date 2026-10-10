package me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aim", type = 'H', complexType = "Negative", experimental = false, description = "Invalid sensitivity.")
public class AimH extends AbstractCheck {

    public AimH(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            double sensitivityEstimate = rotationTracker.getSensitivityEstimate();
            RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
            double deltaPitch = rotationTracker2.getDeltaPitch();
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
            boolean exempt;
            boolean hasKnownSensitivity2;
            exempt = this.isExempt(new ExemptType[] { ExemptType.CINEMATIC });
            RotationTracker rotationTracker3 = this.playerData.getRotationTracker();
            hasKnownSensitivity2 = rotationTracker3.hasKnownSensitivity();
            condition = (sensitivityEstimate < -1.0 && deltaPitch < 20.0 && deltaPitch > 0.25 && deltaPitch > 1.25) && ((playerData.getCollisionState()).getDeltaXZ() > 0.01);
            boolean condition2 = condition;
            if (condition2 && !exempt && !hasKnownSensitivity2) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("final=" + sensitivityEstimate + " deltaPitch=" + deltaPitch);
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
