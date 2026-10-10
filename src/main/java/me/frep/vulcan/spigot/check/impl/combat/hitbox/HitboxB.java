package me.frep.vulcan.spigot.check.impl.combat.hitbox;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Hitbox", type = 'B', complexType = "Simple", description = "Attacked while not looking at target.")
public class HitboxB extends AbstractCheck {

    private boolean pendingAttack;

    private Player targetPlayer;

    public HitboxB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            if (this.playerData.getLastInteractionPacket().getAction() != InteractAction.ATTACK || !(this.playerData.getRelativeMoveTracker().getLastAttackedEntity() instanceof Player)) {
                return;
            }
            this.pendingAttack = true;
        } else if (this.isMovementPacket(packetEvent)) {
            if (this.pendingAttack && this.targetPlayer != null) {
                PlayerData targetData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(this.targetPlayer);
                if (targetData == null) {
                    return;
                }
                double offsetX = targetData.getCollisionState().getX() - this.playerData.getCollisionState().getX();
                double offsetZ = targetData.getCollisionState().getZ() - this.playerData.getCollisionState().getZ();
                float yaw = this.playerData.getRotationTracker().getYaw();
                Vector look = new Vector(-Math.sin(yaw * (float) Math.PI / 180.0F) * 0.5, 0.0, Math.cos(yaw * (float) Math.PI / 180.0F) * 0.5);
                Vector toTarget = new Vector(offsetX, 0.0, offsetZ);
                double distance = this.playerData.getRelativeMoveTracker().getTargetDistance();
                double angle = Math.toDegrees(toTarget.angle(look));
                double maximumAngle = VulcanSettings.checksCombatHitboxBMaxAngle;
                boolean targetExempt = targetData.getPlayerToggles().isAnyExempt(new ExemptType[] { ExemptType.VEHICLE, ExemptType.BOAT });
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.DEATH });
                boolean outsideNearDistance = distance > (ExemptionManager.isAtLeast1_9() ? 0.3 : 0.8);
                if (distance < 1.0) {
                    maximumAngle += 0.45;
                }
                if (angle > maximumAngle && outsideNearDistance && distance < 10.0 && !exempt && !targetExempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("angle=" + angle + " distance=" + distance);
                    }
                } else {
                    this.decayBuffer();
                }
            }
            this.pendingAttack = false;
        }
    }

    @Override
    public void update() {
    }
}
