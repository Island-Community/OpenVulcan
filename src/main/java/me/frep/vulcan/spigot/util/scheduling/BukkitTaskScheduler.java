package me.frep.vulcan.spigot.util.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitTaskScheduler implements Scheduler {

    private final Plugin plugin;

    public BukkitTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPrimaryThread() {
        return Bukkit.isPrimaryThread();
    }

    public boolean isOwnedByCurrentThread(Entity entity) {
        return isPrimaryThread();
    }

    public boolean isOwnedByCurrentThread(Location location) {
        return isPrimaryThread();
    }

    public ScheduledTask runTask(Runnable task) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    public ScheduledTask runTaskLater(Runnable task, long delayTicks) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    public ScheduledTask runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks), true);
    }

    public ScheduledTask runTaskAsynchronously(Runnable task) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    public ScheduledTask runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks));
    }

    public ScheduledTask runTaskTimerAsynchronously(Runnable task, long delayTicks, long periodTicks) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks), true);
    }

    public ScheduledTask runTask(Entity entity, Runnable task) {
        return runTask(task);
    }

    public ScheduledTask runTask(Location location, Runnable task) {
        return runTask(task);
    }

    public void execute(Runnable task) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task);
    }

    public void cancelTasks(Plugin owner) {
        Bukkit.getScheduler().cancelTasks(owner);
    }
}
