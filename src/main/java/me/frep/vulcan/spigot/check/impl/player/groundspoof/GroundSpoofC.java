package me.frep.vulcan.spigot.check.impl.player.groundspoof;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Ground Spoof", type = 'C', complexType = "Spoof", description = "Spoofed OnGround value.")
public class GroundSpoofC extends AbstractCheck {

    public GroundSpoofC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean positionUpdatePacket = this.isPositionUpdatePacket(packetEvent);
        CollisionState collisionState = this.playerData.getCollisionState();
        double deltaY = collisionState.getDeltaY();
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.ANVIL, ExemptType.LIQUID, ExemptType.WATERLOGGED, ExemptType.RIPTIDE, ExemptType.ELYTRA, ExemptType.GLIDING, ExemptType.JOINED, ExemptType.CLIMBABLE, ExemptType.LILY_PAD, ExemptType.SNOW, ExemptType.WORLD_CHANGE, ExemptType.VELOCITY, ExemptType.WEB, ExemptType.NEAR_GROUND, ExemptType.SLOW_FALLING, ExemptType.LEVITATION, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.HONEY, ExemptType.SLIME, ExemptType.FLIGHT, ExemptType.VOID, ExemptType.CANCELLED_PLACE, ExemptType.PLACED_SLIME, ExemptType.BLOCK_PLACE });
        double deltaYDifference;
        int index = (deltaYDifference = deltaY - -0.5) == 0.0 ? 0 : (deltaYDifference < 0.0 ? -1 : 1);
        if (index > 0) {
            this.fail("deltaY=" + deltaY + " airTicks=" + positionUpdatePacket);
            this.decayBuffer();
        }
    }

    @Override
    public void update() {
    }
}
