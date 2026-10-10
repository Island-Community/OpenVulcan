package me.frep.vulcan.spigot.util.scheduling;

import me.frep.vulcan.spigot.util.scheduling.ScheduledTask;
import me.frep.vulcan.spigot.util.scheduling.UniversalScheduler;
import org.bukkit.plugin.Plugin;

public abstract class ScheduledRunnable implements Runnable {

    private ScheduledTask scheduledTask;

    public synchronized void cancel() {
        requireScheduled();
        scheduledTask.cancel();
    }

    public synchronized boolean isCancelled() {
        requireScheduled();
        return scheduledTask.isCancelled();
    }

    public synchronized ScheduledTask runTask(Plugin plugin) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTask(this);
    }

    public synchronized ScheduledTask runTaskAsynchronously(Plugin plugin) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTaskAsynchronously(this);
    }

    public synchronized ScheduledTask runTaskLater(Plugin plugin, long delayTicks) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTaskLater(this, delayTicks);
    }

    public synchronized ScheduledTask runTaskLaterAsynchronously(Plugin plugin, long delayTicks) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTaskLaterAsynchronously(this, delayTicks);
    }

    public synchronized ScheduledTask runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTaskTimer(this, delayTicks, periodTicks);
    }

    public synchronized ScheduledTask runTaskTimerAsynchronously(Plugin plugin, long delayTicks, long periodTicks) {
        requireUnscheduled();
        return scheduledTask = UniversalScheduler.getScheduler(plugin).runTaskTimerAsynchronously(this, delayTicks, periodTicks);
    }

    private void requireScheduled() {
        if (scheduledTask == null)
            throw new IllegalStateException("Not scheduled yet");
    }

    private void requireUnscheduled() {
        if (scheduledTask != null)
            throw new IllegalStateException("Already scheduled");
    }
}
