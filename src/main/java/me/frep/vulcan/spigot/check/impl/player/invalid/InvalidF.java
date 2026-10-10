package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'F', complexType = "Spoofed Y", description = "Impossible Y-axis change.")
public class InvalidF extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public InvalidF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!isPositionUpdatePacket(event))
            return;
        if (isExempt(new ExemptType[] { ExemptType.DEATH, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.WORLD_CHANGE }))
            return;
        double delta = playerData.getCollisionState().getDeltaY();
        if (Math.abs(delta) > 100.0)
            fail("deltaY=" + delta);
        else
            decayBuffer();
    }

    @Override
    public void update() {
    }
}
