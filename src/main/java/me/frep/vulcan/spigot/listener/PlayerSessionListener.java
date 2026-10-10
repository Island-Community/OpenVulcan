package me.frep.vulcan.spigot.listener;

import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSessionListener implements Listener {

    @EventHandler
    public void handlePlayer(PlayerJoinEvent playerJoinEvent) {
        PlayerData playerData;
        Player player = playerJoinEvent.getPlayer();
        if ((player.hasPermission("vulcan.alerts")) && (VulcanSettings.settingsToggleAlertsOnJoin)) {
            PluginServices.INSTANCE.getAlertManager().toggleAlerts(player);
        }
        if (VulcanSettings.joinMessageEnabled) {
            playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
            if (playerData == null) {
                return;
            }
            for (String message : VulcanSettings.joinMessageMessage) PlayerSessionListener.sendJoinMessage(player, playerData, message);
        }
        playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        playerData.getCollisionState().setSetbackX(player.getLocation().getX());
        playerData.getCollisionState().setSetbackY(player.getLocation().getY());
        playerData.getCollisionState().setSetbackZ(player.getLocation().getZ());
        playerData.getCollisionState().setJoinX(player.getLocation().getX());
        playerData.getCollisionState().setJoinY(player.getLocation().getY());
        playerData.getCollisionState().setJoinZ(player.getLocation().getZ());
    }

    @EventHandler
    public void handlePlayer(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        PluginServices.INSTANCE.getAlertManager().getAlertStaff().remove(player);
        PluginServices.INSTANCE.getAlertManager().getVerboseStaff().remove(player);
        PluginServices.INSTANCE.getClientBrands().remove(player.getUniqueId());
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        if ((playerData.getCollisionState().isFrozen())) {
            Object value = VulcanSettings.messagesLoggedOutWhileFrozen.replaceAll("%player%", player.getName());
            PluginServices.INSTANCE.getAlertManager().broadcastAlert(VulcanSettings.messagesLoggedOutWhileFrozen.replaceAll("%player%", player.getName()));
            VulcanSettings.freezeLoggedOutWhileFrozenCommands.forEach(text -> PlayerSessionListener.executeQuitCommand(player, text));
        }
    }

    private static void executeQuitCommand(Player player2, String text) {
        ExemptionManager.dispatchConsoleCommand(ChatColorUtils.colorize(text.replaceAll("%player%", player2.getName())));
    }

    private static void sendJoinMessage(Player player2, PlayerData playerData2, String text) {
        player2.sendMessage(ChatColorUtils.colorize(text.replaceAll("%client-brand%", playerData2.getClientBrand()).replaceAll("%player%", player2.getName())));
    }
}
