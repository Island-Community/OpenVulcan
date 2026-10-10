package me.frep.vulcan.spigot.util.scheduling;

import org.bukkit.plugin.Plugin;

public class UniversalScheduler {

    public static Scheduler getScheduler(Plugin plugin2) {
        if (isAvailable("io.papermc.paper.threadedregions.RegionizedServer") || isAvailable("io.canvasmc.canvas.server.ThreadedServer"))
            return new RegionTaskScheduler(plugin2);
        if (isAvailable("io.papermc.paper.threadedregions.scheduler.ScheduledTask"))
            return new PaperTaskScheduler(plugin2);
        return new BukkitTaskScheduler(plugin2);
    }

    private static boolean isAvailable(String className) {
        try {
            Class.forName(className, false, UniversalScheduler.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException absent) {
            return false;
        }
    }
}
