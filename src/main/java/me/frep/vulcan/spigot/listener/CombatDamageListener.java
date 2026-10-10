package me.frep.vulcan.spigot.listener;

import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatDamageListener implements Listener {

    @EventHandler
    public void handleEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Player entity2 = (Player) entityDamageByEntityEvent.getEntity();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData = playerDataStore.getPlayerData(entity2);
        if (playerData != null) {
            PluginServices.INSTANCE.isMythicMobs5();
        }
    }
}
