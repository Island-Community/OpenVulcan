package me.frep.vulcan.spigot.check.impl.combat.velocity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Velocity", type = 'C', complexType = "Ignored Vertical", description = "Vertical velocity modifications.")
public class VelocityC extends AbstractCheck {

    public VelocityC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        this.isMovementPacket(packetEvent);
    }

    @Override
    public void update() {
    }
}
