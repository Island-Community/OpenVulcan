package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'K', complexType = "Acceleration", description = "Accelerating while ascending.")
public class ElytraK extends AbstractCheck {

    public ElytraK(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting())) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (!motionState.isGliding()) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaY = collisionState.getDeltaY();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaXZ = collisionState2.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            double deltaXZDifference = deltaXZ - previousDeltaXZ;
            int glidingTicks = this.playerData.getCollisionState().getGlidingTicks();
            boolean aboveDeltaYLimit = deltaY > 0.0;
            boolean accel = deltaXZDifference > 0.005;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FIREWORK, ExemptType.EXPLOSION, ExemptType.RIPTIDE, ExemptType.DRAGON_DAMAGE, ExemptType.FLIGHT, ExemptType.VELOCITY, ExemptType.SLIME, ExemptType.DOLPHINS_GRACE, ExemptType.LIQUID });
            if (deltaXZ > 0.3 && aboveDeltaYLimit && accel && !exempt && glidingTicks > 5) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaXZ=" + deltaXZ + " accel=" + accel + " ticks=" + glidingTicks);
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
