package me.frep.vulcan.spigot.player.settings;

import java.util.function.Function;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

public class PlayerToggles {

    private final PlayerData playerData;

    public boolean isExempt(ExemptType exemption) {
        return exemption.getFunction().apply(playerData);
    }

    public boolean isAnyExempt(ExemptType[] exemptions) {
        for (ExemptType exemption : exemptions) if (isExempt(exemption))
            return true;
        return false;
    }

    public boolean matches(Function function2) {
        return (Boolean) function2.apply(this.playerData);
    }

    public PlayerToggles(PlayerData playerData2) {
        this.playerData = playerData2;
    }
}
