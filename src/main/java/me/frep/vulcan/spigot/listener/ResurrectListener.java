package me.frep.vulcan.spigot.listener;

import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

public class ResurrectListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void handleEvent(EntityResurrectEvent entityResurrectEvent) {
        if (entityResurrectEvent.getEntity() instanceof Player) {
            Player entity2 = (Player) entityResurrectEvent.getEntity();
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData = playerDataStore.getPlayerData(entity2);
            if (playerData == null) {
                return;
            }
            MotionState motionState = playerData.getMotionState();
        }
    }
}
