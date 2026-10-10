package me.frep.vulcan.spigot.check.impl.player.badpackets.movement;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'D', complexType = "Timer", description = "Too many flying packets.", experimental = true)
public class BadPacketsD extends AbstractCheck {

    public BadPacketsD(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void applyConfiguredMitigation() {
    }
}
