package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'C', complexType = "Y", description = "Too large Y-Axis movement.")
public class InvalidC extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public InvalidC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!isPositionUpdatePacket(event))
            return;
        if (isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.RIPTIDE, ExemptType.GLIDING, ExemptType.FISHING_ROD, ExemptType.VELOCITY, ExemptType.JOINED, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.WORLD_CHANGE, ExemptType.DOLPHINS_GRACE, ExemptType.SOUL_SPEED, ExemptType.DEATH, ExemptType.SLEEPING, ExemptType.ENDER_PEARL, ExemptType.HIGH_JUMP_BOOST, ExemptType.HIGH_LEVITATION }) || playerData.getVelocityTracker().isPendingVelocity() || playerData.getVelocityTracker().isPendingExplosion())
            return;
        if (System.currentTimeMillis() - playerData.getJoinTime() <= 10000L)
            return;
        double delta = playerData.getCollisionState().getDeltaY();
        if (Math.abs(delta) > 4.0)
            fail("deltaY=" + delta);
        else
            decayBuffer();
    }

    @Override
    public void update() {
    }
}
