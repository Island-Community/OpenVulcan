package me.frep.vulcan.spigot.util.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperTaskScheduler extends RegionTaskScheduler {

    public PaperTaskScheduler(Plugin plugin2) {
        super(plugin2);
    }

    @Override
    public boolean isPrimaryThread() {
        return Bukkit.getServer().isPrimaryThread();
    }
}
