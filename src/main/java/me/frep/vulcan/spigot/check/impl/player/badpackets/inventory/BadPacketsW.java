package me.frep.vulcan.spigot.check.impl.player.badpackets.inventory;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'W', complexType = "Post WindowClick", experimental = false, description = "Post WindowClick packets.")
public class BadPacketsW extends AbstractCheck {

    public BadPacketsW(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void applyConfiguredMitigation() {
    }
}
