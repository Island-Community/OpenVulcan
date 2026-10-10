package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'M', complexType = "Ascending", description = "Invalid ascension pattern.")
public class ElytraM extends AbstractCheck {

    public ElytraM(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting())) {
            MotionState motionState = this.playerData.getMotionState();
            if (!motionState.isGliding()) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaY = collisionState.getDeltaY();
            if (Math.abs(deltaY) > 0.01) {
                double deltaYRemainderRemainder = 0.01 % (deltaY % 0.01);
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.SLIME, ExemptType.SLOW_FALLING, ExemptType.LEVITATION, ExemptType.LIQUID, ExemptType.BUBBLE_COLUMN, ExemptType.LIQUID, ExemptType.RIPTIDE, ExemptType.CLIMBABLE });
                if (deltaYRemainderRemainder < 1.0E-8 && !exempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("deltaY=" + deltaY + " m=" + deltaYRemainderRemainder);
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
