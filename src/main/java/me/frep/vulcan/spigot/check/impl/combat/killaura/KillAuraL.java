package me.frep.vulcan.spigot.check.impl.combat.killaura;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import org.bukkit.entity.Player;

@CheckInfo(name = "Kill Aura", type = 'L', complexType = "Strafe", experimental = true, description = "Impossible strafe.")
public class KillAuraL extends AbstractCheck {

    public KillAuraL(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        boolean condition2;
        if ((this.isPositionUpdatePacket(packetEvent)) && (this.getAttackTicks() < 3)) {
            if (this.playerData.getPlayer() == null) {
                return;
            }
            double deltaXZ;
            double deltaXZChange;
            float maxSpeed;
            long clickInterval;
            boolean sprinting;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaXZ = collisionState.getDeltaXZ();
            deltaXZChange = this.playerData.getCollisionState().getDeltaXZChange();
            maxSpeed = CheckUtils.getMaxSpeed(this.playerData, 0.275F);
            ClickTimingData clickTimingData = this.playerData.getClickTimingData();
            clickInterval = clickTimingData.getClickInterval();
            MotionState motionState = this.playerData.getMotionState();
            sprinting = motionState.isSprinting();
            condition2 = ((this.playerData.getRelativeMoveTracker()).getLastAttackedEntity() != null) && ((this.playerData.getRelativeMoveTracker()).getLastAttackedEntity() instanceof Player);
            boolean condition3;
            boolean olderThan;
            condition3 = condition2;
            olderThan = this.playerData.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_9);
            if (this.playerData.getPlayer().getMaximumNoDamageTicks() >= 18) {
                if (condition3) {
                    RelativeMoveTracker relativeMoveTracker3 = this.playerData.getRelativeMoveTracker();
                    if (((Player) relativeMoveTracker3.getLastAttackedEntity()).getMaximumNoDamageTicks() < 18) {
                        condition = true;
                    } else {
                        condition = false;
                    }
                } else {
                    condition = false;
                }
            } else {
                condition = true;
            }
            boolean condition4 = condition;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SOUL_SAND });
            boolean condition5 = deltaXZChange < 0.0025 && !sprinting && deltaXZ > maxSpeed && clickInterval < 500L && condition3 && olderThan;
            if (condition5 && !exempt && !condition4) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("d=" + deltaXZ + " a=" + deltaXZChange + " s=" + clickInterval);
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
