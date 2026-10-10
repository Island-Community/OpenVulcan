package me.frep.vulcan.spigot.integration.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.frep.vulcan.spigot.VulcanPlugin;
import me.frep.vulcan.spigot.config.StatisticsFile;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;

/**
 * Expands the player and violation placeholders exposed by Vulcan.
 */
public final class PlaceholderHook extends PlaceholderExpansion {

    private final VulcanPlugin plugin;

    // Preserved solely for the shared codec initialization side effect.
    public PlaceholderHook() {
        this.plugin = PluginServices.INSTANCE.getVulcanPlugin();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "Vulcan";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null) {
            return "";
        }
        PlayerData data = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
        if (data == null) {
            return "";
        }
        if (placeholder == null) {
            return null;
        }
        return switch(placeholder) {
            case "cps" ->
                Double.toString(data.getClickTimingData().getClicksPerSecond());
            case "total_violations" ->
                Integer.toString(data.getTotalViolations());
            case "combat_violations" ->
                Integer.toString(data.getCombatViolations());
            case "movement_violations" ->
                Integer.toString(data.getMovementViolations());
            case "player_violations" ->
                Integer.toString(data.getPlayerViolations());
            case "client_brand" ->
                data.getClientBrand();
            case "total_punishments" ->
                Integer.toString(StatisticsFile.getPunishmentCount());
            default ->
                null;
        };
    }
}
