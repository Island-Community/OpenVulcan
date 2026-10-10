package me.frep.vulcan.spigot.check.impl.movement.jump;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;

@CheckInfo(name = "Jump", type = 'B', complexType = "Height", description = "Invalid jump motion.")
public class JumpB extends AbstractCheck {

    public JumpB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaY = collisionState.getDeltaY();
            this.playerData.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);
            this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.RIPTIDE, ExemptType.VEHICLE, ExemptType.HONEY, ExemptType.GLIDING, ExemptType.LEVITATION, ExemptType.SLAB, ExemptType.FLIGHT, ExemptType.JUMP_BOOST_RAN_OUT, ExemptType.BOAT, ExemptType.BUBBLE_COLUMN, ExemptType.WALL, ExemptType.CREATIVE, ExemptType.SHULKER_BOX, ExemptType.ANVIL, ExemptType.SLIME, ExemptType.PISTON, ExemptType.TURTLE_EGG, ExemptType.DOOR, ExemptType.DEATH, ExemptType.ENDER_PEARL, ExemptType.PLACED_SLIME, ExemptType.CHORUS_FRUIT, ExemptType.MYTHIC_MOB, ExemptType.BLOCK_BREAK, ExemptType.FISHING_ROD, ExemptType.SLEEPING, ExemptType.SHULKER });
            if (ExemptionManager.isAtLeast1_9()) {
                MotionState motionState = this.playerData.getMotionState();
                double jumpStrength = motionState.getJumpStrength();
                VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
                if (velocityTracker.getTicksSinceUnloadedPosition() < 5) {
                    MotionState motionState2 = this.playerData.getMotionState();
                    double jumpStrength2 = motionState2.getJumpStrength();
                    MotionState motionState3 = this.playerData.getMotionState();
                    jumpStrength += Math.max(jumpStrength2, motionState3.getPreviousJumpStrength());
                }
                VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
                byte index = (byte) (velocityTracker2.isPendingExplosion() ? 1 : 0);
                if (index > 0 || deltaY - jumpStrength > 1.0) {
                    StringBuilder stringBuilder = new StringBuilder().append("deltaY=").append(deltaY).append(" limit=").append(jumpStrength).append(" tp=");
                    MotionState motionState4 = this.playerData.getMotionState();
                    StringBuilder stringBuilder2 = stringBuilder.append(motionState4.getTicksSinceServerPosition()).append(" vY=");
                    VelocityTracker velocityTracker6 = this.playerData.getVelocityTracker();
                    StringBuilder stringBuilder3 = stringBuilder2.append(velocityTracker6.getVelocityY()).append(" eY=");
                    VelocityTracker velocityTracker7 = this.playerData.getVelocityTracker();
                    this.fail(stringBuilder3.append(velocityTracker7.getCombinedExplosionY()).toString());
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
