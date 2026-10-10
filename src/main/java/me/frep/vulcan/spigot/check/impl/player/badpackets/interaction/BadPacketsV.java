package me.frep.vulcan.spigot.check.impl.player.badpackets.interaction;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'V', complexType = "Null Vehicle", description = "Spoofed SteerVehicle packets.")
public class BadPacketsV extends AbstractCheck {

    public BadPacketsV(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void applyConfiguredMitigation() {
    }
}
