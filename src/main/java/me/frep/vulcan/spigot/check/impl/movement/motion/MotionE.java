package me.frep.vulcan.spigot.check.impl.movement.motion;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Motion", type = 'E', complexType = "Modulo", experimental = false, description = "Impossible movement values.")
public class MotionE extends AbstractCheck {

    public MotionE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            if (this.playerData.getPlayer() == null) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.isSlownessEffect()) {
                return;
            }
            if (this.playerData.getPlayer().getWalkSpeed() > 0.25) {
                return;
            }
            MotionState motionState2 = this.playerData.getMotionState();
            if (motionState2.getSpeedAmplifier() > 3) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double deltaXZRemainderRemainder = 0.1 % (deltaXZ % 0.1);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.RIPTIDE, ExemptType.GLIDING, ExemptType.STAIRS, ExemptType.VELOCITY, ExemptType.JOINED, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.WORLD_CHANGE, ExemptType.DOLPHINS_GRACE, ExemptType.SOUL_SPEED, ExemptType.SLIME, ExemptType.HONEY, ExemptType.DEATH, ExemptType.RIPTIDE, ExemptType.SLEEPING, ExemptType.LEVITATION, ExemptType.LIQUID, ExemptType.ANVIL, ExemptType.SERVER_POSITION, ExemptType.PISTON, ExemptType.WEB });
            boolean condition = deltaXZ > 0.11 && deltaXZRemainderRemainder < 1.0E-8;
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaXZ=" + deltaXZ + " modulo=" + deltaXZRemainderRemainder);
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
