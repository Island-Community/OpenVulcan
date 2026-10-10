package me.frep.vulcan.spigot.check.impl.movement.antilevitation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Anti Levitation", type = 'A', complexType = "Ignore", description = "Ignored Levitation effect.")
public class AntiLevitationA extends AbstractCheck {

    public AntiLevitationA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting())) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getLevitationAmplifier() < 0) {
                return;
            }
            double deltaY;
            boolean onGround;
            boolean condition2;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaY = collisionState.getDeltaY();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            boolean groundAligned = collisionState2.isGroundAligned();
            CollisionState collisionState3 = this.playerData.getCollisionState();
            onGround = collisionState3.isOnGround();
            MotionState motionState2 = this.playerData.getMotionState();
            boolean packetLevitationEffect = motionState2.isPacketLevitationEffect();
            CollisionState collisionState4 = this.playerData.getCollisionState();
            boolean inOpenAir = collisionState4.isInOpenAir();
            condition2 = packetLevitationEffect && (onGround || deltaY < 0.0 || !inOpenAir && groundAligned);
            condition = !((!this.isExempt(new ExemptType[] { ExemptType.SCAFFOLDING, ExemptType.VOID, ExemptType.COLLIDING_VERTICALLY, ExemptType.TRAPDOOR, ExemptType.BED, ExemptType.SLIME, ExemptType.HONEY, ExemptType.VELOCITY, ExemptType.RIPTIDE, ExemptType.JOINED, ExemptType.SWIMMING, ExemptType.CHUNK, ExemptType.JOINED_CHUNK_LOAD, ExemptType.WEB, ExemptType.SLEEPING, ExemptType.DEATH, ExemptType.FROZEN, ExemptType.BLOCK_PLACE, ExemptType.BLOCK_BREAK, ExemptType.FULLY_STUCK, ExemptType.PARTIALLY_STUCK, ExemptType.WORLD_CHANGE, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.GLIDING, ExemptType.SLOW_FALLING, ExemptType.BUBBLE_COLUMN, ExemptType.SPECTATOR, ExemptType.DOLPHINS_GRACE, ExemptType.SHULKER_BOX, ExemptType.FENCE, ExemptType.FENCE_GATE, ExemptType.STAIRS })) && (!this.playerData.getCollisionState().isExemptTeleport()) && (!this.playerData.getCollisionState().isExemptClimbable()) && (!this.playerData.getCollisionState().isExemptFlight()) && (!this.playerData.getCollisionState().isExemptCreative()) && (!this.playerData.getCollisionState().isExemptLiquid()) && (!this.playerData.getCollisionState().isExemptElytra()));
            boolean condition3 = condition;
            if (condition2 && !condition3) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    StringBuilder stringBuilder = new StringBuilder().append("deltaY=").append(deltaY).append(" onGround=").append(onGround).append(" amplifier=");
                    MotionState motionState3 = this.playerData.getMotionState();
                    this.fail(stringBuilder.append(motionState3.getLevitationAmplifier()).toString());
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
