package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'C', complexType = "Acceleration", description = "Invalid acceleration.")
public class ElytraC extends AbstractCheck {

    public ElytraC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (!motionState.isGliding()) {
                return;
            }
            int glidingTicks = this.playerData.getCollisionState().getGlidingTicks();
            CollisionState collisionState = this.playerData.getCollisionState();
            int ghostActivityTicks = collisionState.getGhostActivityTicks();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            double deltaXZ = collisionState3.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            boolean gliding = CheckUtils.isGliding(this.playerData);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FIREWORK, ExemptType.EXPLOSION, ExemptType.RIPTIDE, ExemptType.CHUNK, ExemptType.DRAGON_DAMAGE, ExemptType.FLIGHT, ExemptType.SLIME });
            double accel = deltaXZ - previousDeltaXZ;
            if (glidingTicks > 3 && ghostActivityTicks > 3 && gliding && !exempt && accel > 0.035 && deltaY > -0.6) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("accel=" + accel + " deltaXZ=" + deltaXZ + " deltaY=" + deltaY);
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
