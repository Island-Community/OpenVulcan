package me.frep.vulcan.spigot.check.impl.movement.motion;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Motion", type = 'A', complexType = "Constant", description = "Repeated vertical motions.")
public class MotionA extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public MotionA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaY = collisionState.getDeltaY();
            double previousDeltaY = this.playerData.getCollisionState().getPreviousDeltaY();
            boolean belowDeltaYDifferenceLimit = Math.abs(deltaY - 0.11760000228882461) <= 0.001;
            if (belowDeltaYDifferenceLimit) {
                return;
            }
            boolean recentClimbable;
            CollisionState collisionState2 = this.playerData.getCollisionState();
            recentClimbable = collisionState2.getTicksSinceClimbable() < 20;
            condition = (this.isExempt(new ExemptType[] { ExemptType.HALF_BLOCK, ExemptType.CLIMBABLE, ExemptType.FLIGHT, ExemptType.BED, ExemptType.LIQUID, ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.ILLEGAL_BLOCK, ExemptType.SLEEPING, ExemptType.SOUL_SAND, ExemptType.HONEY, ExemptType.SLIME, ExemptType.LEVITATION, ExemptType.SPECTATOR, ExemptType.SCAFFOLDING, ExemptType.FENCE, ExemptType.BUBBLE_COLUMN, ExemptType.CREATIVE, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.ELYTRA, ExemptType.GLIDING, ExemptType.CANCELLED_PLACE, ExemptType.LENIENT_SCAFFOLDING, ExemptType.PLACED_CLIMBABLE, ExemptType.KELP, ExemptType.BOAT, ExemptType.CHUNK, ExemptType.TRAPDOOR, ExemptType.POWDER_SNOW, ExemptType.EMPTIED_BUCKET, ExemptType.CAKE, ExemptType.WEB })) || (((this.playerData.getCollisionState()).isNearHeavyCore()) || ((this.playerData.getCollisionState()).isFlowerPot()));
            boolean condition4 = condition;
            boolean condition5 = deltaY > 0.0 && deltaY == previousDeltaY;
            if (condition5 && !condition4 && !recentClimbable) {
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
