package me.frep.vulcan.spigot.packet;

import com.github.retrooper.packetevents.protocol.player.User;
import me.frep.vulcan.spigot.check.manager.CheckManager;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;

public class UserDataInitializer {

    public void initializePlayer(User user) {
        PlayerDataStore store = PluginServices.INSTANCE.getPlayerDataStore();
        if (store.getPlayerData(user) == null)
            store.register(user);
        PlayerData data = store.getPlayerData(user);
        data.setUniqueId(user.getUUID());
        data.setClientVersion(user.getClientVersion());
        data.setPlayerName(user.getName());
        data.setPlayer(org.bukkit.Bukkit.getPlayer(user.getUUID()));
        data.setChecks(CheckManager.createChecks(data));
    }
}
