package me.frep.vulcan.spigot.listener;

import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRiptideEvent;

public class RiptideListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerRiptideEvent playerRiptideEvent) {
        Player player2 = playerRiptideEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData = playerDataStore.getPlayerData(player2);
        if (playerData != null) {
            CollisionState collisionState = playerData.getCollisionState();
            collisionState.setTicksSinceRiptide((Integer) 0);
        }
    }
}
