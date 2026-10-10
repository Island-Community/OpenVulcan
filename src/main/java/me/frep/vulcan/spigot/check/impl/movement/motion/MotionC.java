package me.frep.vulcan.spigot.check.impl.movement.motion;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Motion", type = 'C', complexType = "Jump", description = "Invalid jump motion.")
public class MotionC extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public MotionC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting())) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaY = Math.abs(collisionState.getDeltaY());
            double deltaYDifference = Math.abs(deltaY - 0.42F);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JUMP_BOOST, ExemptType.VELOCITY, ExemptType.FLIGHT, ExemptType.ILLEGAL_BLOCK, ExemptType.CREATIVE, ExemptType.SLIME, ExemptType.GLIDING, ExemptType.WEB, ExemptType.VEHICLE, ExemptType.TELEPORT, ExemptType.SCAFFOLDING, ExemptType.LIQUID, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.ELYTRA });
            boolean condition = deltaYDifference < 1.0E-5 && deltaYDifference > 0.0 && deltaY > 0.1;
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + deltaY);
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
