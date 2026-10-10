package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'A', complexType = "Invalid", description = "Possible transaction disabler. Do not ban for this check.")
public class InvalidA extends AbstractCheck {

    private int serverFlightMismatchTicks;

    public InvalidA(PlayerData playerData) {
        super(playerData);
        this.serverFlightMismatchTicks = 0;
    }

    public void handlePacket(PacketEvent event) {
        if (!isPositionUpdatePacket(event))
            return;
        if (playerData.getPlayer() != null && playerData.getCollisionState().isFlightAllowed() && !playerData.getPlayer().getAllowFlight()) {
            serverFlightMismatchTicks++;
        } else {
            serverFlightMismatchTicks = 0;
        }
        if (serverFlightMismatchTicks > 100) {
            fail();
        }
    }

    @Override
    public void update() {
    }
}
