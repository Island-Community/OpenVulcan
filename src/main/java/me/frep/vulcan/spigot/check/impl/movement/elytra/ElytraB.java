package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'B', complexType = "Acceleration", description = "Invalid Y-Axis change.")
public class ElytraB extends AbstractCheck {

    public ElytraB(PlayerData playerData) {
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
            double deltaXZ = collisionState.getDeltaXZ();
            double previousDeltaXZ = this.playerData.getCollisionState().getPreviousDeltaXZ();
            double deltaXZDifference = Math.abs(deltaXZ - previousDeltaXZ);
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            double previousDeltaY = this.playerData.getCollisionState().getPreviousDeltaY();
            double deltaYDifference = Math.abs(deltaY - previousDeltaY);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FIREWORK, ExemptType.LIQUID });
            boolean condition = glidingTicks > 5 && glidingTicks < 400 && deltaXZDifference < 1.0E-10 && deltaYDifference < 1.0E-10 && deltaXZ > 1.0;
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("ticks=" + glidingTicks + " deltaXZ=" + deltaXZ + " deltaY=" + deltaY + " accelXZ=" + deltaXZDifference + " accelY=" + deltaYDifference);
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
