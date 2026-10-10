package me.frep.vulcan.spigot.config;

import java.io.File;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import org.bukkit.plugin.java.JavaPlugin;

public final class StatisticsFile {

    private static VulcanConfig configuration;

    private static File file;

    private StatisticsFile() {
    }

    public static void save() {
        configuration.save(file);
    }

    public static void reload() {
        configuration.load(file);
    }

    public static void initialize() {
        JavaPlugin plugin = PluginServices.INSTANCE.getVulcanPlugin();
        file = new File(plugin.getDataFolder(), "stats.yml");
        if (!file.exists())
            plugin.saveResource("stats.yml", false);
        configuration = VulcanConfig.loadConfiguration(file);
        configuration.mergeBundledDefaults(file, plugin.getResource("stats.yml"), new String[] { "NONE" });
    }

    public static int getInt(String path) {
        return configuration.getInt(path);
    }

    public static int getPunishmentCount() {
        return getInt("punishments");
    }

    public static void setAndSave(String path, Object value) {
        configuration.set(path, value);
        configuration.save(file);
    }
}
