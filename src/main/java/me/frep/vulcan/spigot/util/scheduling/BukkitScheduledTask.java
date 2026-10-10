package me.frep.vulcan.spigot.util.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitScheduledTask implements ScheduledTask {

    private final BukkitTask task;

    private final boolean repeating;

    public BukkitScheduledTask(BukkitTask task) {
        this(task, false);
    }

    public BukkitScheduledTask(BukkitTask task, boolean repeating) {
        this.task = task;
        this.repeating = repeating;
    }

    public void cancel() {
        task.cancel();
    }

    public boolean isCancelled() {
        return task.isCancelled();
    }

    public Plugin getOwner() {
        return task.getOwner();
    }

    public boolean isRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId());
    }

    public boolean isRepeating() {
        return repeating;
    }
}
