package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'I', complexType = "Motion", description = "Invalid motion.")
public class ElytraI extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaXZ=";

    public ElytraI(PlayerData playerData) {
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
            double deltaXZ = collisionState.getDeltaXZ();
            double deltaXZRemainderRemainder = 0.1 % (deltaXZ % 0.1);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FIREWORK, ExemptType.EXPLOSION, ExemptType.RIPTIDE, ExemptType.WEB, ExemptType.DRAGON_DAMAGE, ExemptType.FLIGHT, ExemptType.VELOCITY, ExemptType.LIQUID });
            if (deltaXZRemainderRemainder < 1.0E-8 && !exempt && deltaXZ > 0.1) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + deltaXZ);
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
