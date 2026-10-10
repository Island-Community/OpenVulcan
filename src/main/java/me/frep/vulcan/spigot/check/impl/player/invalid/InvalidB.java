package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'B', complexType = "Invalid", description = "Spamming ghost block setbacks. Do not ban for this check.")
public class InvalidB extends AbstractCheck {

    public InvalidB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent) && this.playerData.getCollisionState().getGhostBlockSetbacks() > 5) {
            this.fail();
        }
    }

    @Override
    public void update() {
    }
}
