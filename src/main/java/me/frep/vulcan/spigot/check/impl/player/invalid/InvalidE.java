package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'E', complexType = "X/Z", description = "Too large X/Z-Axis movement.")
public class InvalidE extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaXZ=";

    public InvalidE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!isPositionUpdatePacket(event))
            return;
        if (isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.RIPTIDE, ExemptType.GLIDING, ExemptType.VELOCITY, ExemptType.JOINED, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.WORLD_CHANGE, ExemptType.DOLPHINS_GRACE, ExemptType.SOUL_SPEED, ExemptType.CHORUS_FRUIT, ExemptType.SLEEPING, ExemptType.LEVITATION, ExemptType.ENDER_PEARL, ExemptType.ENTITY_CRAM_FIX, ExemptType.HIGH_SPEED, ExemptType.SHULKER_BOX, ExemptType.SHULKER, ExemptType.CHUNK }) || playerData.getVelocityTracker().isPendingVelocity() || playerData.getVelocityTracker().isPendingExplosion())
            return;
        double delta = playerData.getCollisionState().getDeltaXZ();
        if (Math.abs(delta) > 3.5)
            fail("deltaXZ=" + delta);
        else
            decayBuffer();
    }

    @Override
    public void update() {
    }
}
