package me.frep.vulcan.spigot.check.impl.player.groundspoof;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Ground Spoof", type = 'A', complexType = "Spoof", description = "Spoofed onGround value.")
public class GroundSpoofA extends AbstractCheck {

    public GroundSpoofA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean movementPacket = this.isMovementPacket(packetEvent);
        CollisionState collisionState = this.playerData.getCollisionState();
        byte index = (byte) (collisionState.isInOpenAir() ? 1 : 0);
        CollisionState collisionState2 = this.playerData.getCollisionState();
        double deltaY = collisionState2.getDeltaY();
        double deltaYDifferenceDifference;
        int index2 = (deltaYDifferenceDifference = Math.abs(deltaY) - 0.3116 - 0.005) == 0.0 ? 0 : (deltaYDifferenceDifference < 0.0 ? -1 : 1);
        int groundTicks = this.playerData.getCollisionState().getGroundTicks();
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.STAIRS, ExemptType.CHUNK, ExemptType.SHULKER, ExemptType.STAIRS, ExemptType.RIPTIDE, ExemptType.LIQUID, ExemptType.TELEPORT, ExemptType.VOID, ExemptType.WORLD_CHANGE, ExemptType.SCAFFOLDING, ExemptType.RIPTIDE, ExemptType.SWIMMING, ExemptType.BOAT, ExemptType.GLIDING, ExemptType.WATERLOGGED, ExemptType.HONEY, ExemptType.SLOW_FALLING, ExemptType.BLOCK_PLACE, ExemptType.SNOW, ExemptType.WEB, ExemptType.FLIGHT, ExemptType.SLIME, ExemptType.PLACED_SLIME, ExemptType.HAPPY_GHAST, ExemptType.SHULKER_BOX, ExemptType.NEAR_GROUND, ExemptType.CANCELLED_PLACE, ExemptType.LEVITATION });
        if (index > 0) {
            this.fail("deltaY=" + deltaY + " ticks=" + groundTicks);
            this.decayBuffer();
        }
    }

    @Override
    public void update() {
    }
}
