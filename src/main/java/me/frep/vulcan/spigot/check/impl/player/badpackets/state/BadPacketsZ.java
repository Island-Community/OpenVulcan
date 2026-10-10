package me.frep.vulcan.spigot.check.impl.player.badpackets.state;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.GameMode;

@CheckInfo(name = "Bad Packets", type = 'Z', complexType = "Spectate", description = "Invalid Spectate Packets.")
public class BadPacketsZ extends AbstractCheck {

    public BadPacketsZ(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if (this.isSpectatePacket(packetEvent)) {
            if (ExemptionManager.isBefore1_8()) {
                return;
            }
            if (this.playerData.getPlayer() == null) {
                return;
            }
            condition = ((this.playerData.getPlayer().getGameMode() != GameMode.SPECTATOR) && (this.playerData.getMotionState().getGameMode() != GameMode.SPECTATOR));
            boolean condition2 = condition;
            if (condition2) {
                this.fail();
            }
        }
    }

    @Override
    public void update() {
    }
}
