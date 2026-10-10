package me.frep.vulcan.spigot.check.impl.movement.step;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Step", type = 'A', complexType = "Vanilla", description = "Invalid Step height.")
public class StepA extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public StepA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting())) {
            double deltaY;
            boolean exempt;
            boolean condition;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaY = collisionState.getDeltaY();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean onGround = collisionState2.isOnGround();
            exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.SHULKER_BOX, ExemptType.SPECTATOR, ExemptType.WORLD_CHANGE, ExemptType.RIPTIDE, ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.SHULKER, ExemptType.ENDER_PEARL, ExemptType.SLEEPING, ExemptType.BED, ExemptType.BLOCK_BREAK });
            if (onGround) {
                double stepHeight;
                if (this.playerData.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2)) {
                    MotionState motionState = this.playerData.getMotionState();
                    stepHeight = motionState.getStepHeight();
                } else {
                    stepHeight = 0.6F;
                }
                if (deltaY > stepHeight) {
                    condition = true;
                } else {
                    condition = false;
                }
            } else {
                condition = false;
            }
            boolean condition2 = condition;
            if (condition2 && !exempt) {
                this.fail(VIOLATION_DETAIL_PREFIX + deltaY);
            }
        }
    }

    @Override
    public void update() {
    }
}
