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
import org.bukkit.entity.Player;

@CheckInfo(name = "Kill Aura", type = 'B', complexType = "Acceleration", experimental = false, description = "Invalid acceleration.")
public class KillAuraB extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "a=";

    public KillAuraB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
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
            maxSpeed = CheckUtils.getMaxSpeed(this.playerData, 0.21F);
            ClickTimingData clickTimingData = this.playerData.getClickTimingData();
            clickInterval = clickTimingData.getClickInterval();
            MotionState motionState = this.playerData.getMotionState();
            sprinting = motionState.isSprinting();
            condition = ((this.playerData.getRelativeMoveTracker()).getLastAttackedEntity() != null) && ((this.playerData.getRelativeMoveTracker()).getLastAttackedEntity() instanceof Player);
            boolean condition2 = condition;
            boolean olderThan = this.playerData.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_9);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SOUL_SAND });
            boolean condition3 = deltaXZChange < 0.0025 && sprinting && deltaXZ > maxSpeed && clickInterval < 500L && condition2 && olderThan;
            if (condition3 && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + deltaXZChange);
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
