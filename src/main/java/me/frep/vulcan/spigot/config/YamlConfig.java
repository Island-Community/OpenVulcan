package me.frep.vulcan.spigot.config;

import java.io.File;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import org.bukkit.configuration.file.FileConfiguration;

public class YamlConfig {

    private final File file;

    private final VulcanConfig fileConfiguration;

    public YamlConfig(String text) {
        this.file = new File(PluginServices.INSTANCE.getVulcanPlugin().getDataFolder() + File.separator, text + ".yml");
        this.fileConfiguration = this.file.exists() ? VulcanConfig.loadConfiguration(this.file) : new VulcanConfig();
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    public void save() {
        this.fileConfiguration.save(this.file);
    }
}
