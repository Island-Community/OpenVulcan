package me.frep.vulcan.spigot.check.impl.player.groundspoof;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Ground Spoof", type = 'B', complexType = "Spoof", description = "Spoofed OnGround value.")
public class GroundSpoofB extends AbstractCheck {

    public GroundSpoofB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean positionUpdatePacket = this.isPositionUpdatePacket(packetEvent);
        CollisionState collisionState = this.playerData.getCollisionState();
        byte client = (byte) (collisionState.isOnGround() ? 1 : 0);
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.CHUNK, ExemptType.CARPET, ExemptType.VEHICLE, ExemptType.SCAFFOLDING, ExemptType.DEAD, ExemptType.HAPPY_GHAST, ExemptType.GLIDING });
        CollisionState collisionState3 = this.playerData.getCollisionState();
        double y = collisionState3.getY();
        double yRemainderDifference;
        int index2 = (yRemainderDifference = y % 1.0 - 0.6) == 0.0 ? 0 : (yRemainderDifference < 0.0 ? -1 : 1);
        if (client > 0) {
            this.fail("client=" + client + " server=" + positionUpdatePacket);
            this.decayBuffer();
        }
    }

    @Override
    public void update() {
    }
}
