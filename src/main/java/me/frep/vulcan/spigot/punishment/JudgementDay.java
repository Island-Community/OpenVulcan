package me.frep.vulcan.spigot.punishment;

import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.config.YamlConfig;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;

public class JudgementDay {

    public static boolean isQueued(org.bukkit.entity.Player player) {
        return player != null && new YamlConfig("jday-pending").getFileConfiguration().isConfigurationSection("PendingUsers." + player.getUniqueId());
    }

    public static void execute() {
        PluginServices.INSTANCE.getJudgementExecutor().execute(() -> JudgementDay.executePendingBans());
    }

    public static int pendingCount() {
        var users = new YamlConfig("jday-pending").getFileConfiguration().getConfigurationSection("PendingUsers");
        return users == null ? 0 : users.getKeys(false).size();
    }

    private static void executePendingBans() {
        YamlConfig pending = new YamlConfig("jday-pending");
        YamlConfig banned = new YamlConfig("jday-banned");
        org.bukkit.configuration.ConfigurationSection users = pending.getFileConfiguration().getConfigurationSection("PendingUsers");
        if (users == null)
            return;
        int executed = 0;
        for (String id : new java.util.ArrayList<>(users.getKeys(false))) {
            String name = users.getString(id + ".Name");
            if (name == null)
                continue;
            MainThreadExecutor.execute(() -> {
                for (String command : VulcanSettings.judgementDaysCommands) ExemptionManager.dispatchConsoleCommand(command.replace("%player%", name));
            });
            for (String line : VulcanSettings.judgementDaysBroadcast) ExemptionManager.broadcast(line.replace("%player%", name));
            for (String field : java.util.List.of("Name", "UUID", "Date", "Reason", "ExecutedBy", "wasOnline")) {
                banned.getFileConfiguration().set("BannedUsers." + id + "." + field, users.get(id + "." + field));
            }
            banned.save();
            pending.getFileConfiguration().set("PendingUsers." + id, null);
            pending.save();
            executed++;
            try {
                Thread.sleep(Math.max(0L, VulcanSettings.judgementDaysCooldown));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        for (String line : VulcanSettings.judgementDaysEndedBroadcast) ExemptionManager.broadcast(line.replace("%amount%", Integer.toString(executed)));
        PluginServices.INSTANCE.getVulcanPlugin().saveConfig();
    }
}
