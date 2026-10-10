package me.frep.vulcan.spigot.util.scheduling;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class RegionTaskScheduler implements Scheduler {

    private final Plugin plugin;

    public RegionTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPrimaryThread() {
        try {
            return (Boolean) Bukkit.getServer().getClass().getMethod("isGlobalTickThread").invoke(Bukkit.getServer());
        } catch (NoSuchMethodException absent) {
            return Bukkit.isPrimaryThread();
        } catch (IllegalAccessException | InvocationTargetException failure) {
            throw new IllegalStateException("Cannot query the global tick thread", failure);
        }
    }

    public boolean isOwnedByCurrentThread(Entity entity) {
        return Bukkit.isOwnedByCurrentRegion(entity);
    }

    public boolean isOwnedByCurrentThread(Location location) {
        return Bukkit.isOwnedByCurrentRegion(location);
    }

    public ScheduledTask runTask(Runnable task) {
        return new PaperScheduledTask(Bukkit.getGlobalRegionScheduler().run(plugin, scheduled -> task.run()));
    }

    public ScheduledTask runTaskLater(Runnable task, long delayTicks) {
        if (delayTicks <= 0)
            return runTask(task);
        return new PaperScheduledTask(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduled -> task.run(), delayTicks));
    }

    public ScheduledTask runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        return new PaperScheduledTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduled -> task.run(), Math.max(1, delayTicks), periodTicks));
    }

    public ScheduledTask runTaskAsynchronously(Runnable task) {
        return new PaperScheduledTask(Bukkit.getAsyncScheduler().runNow(plugin, scheduled -> task.run()));
    }

    public ScheduledTask runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        return new PaperScheduledTask(Bukkit.getAsyncScheduler().runDelayed(plugin, scheduled -> task.run(), Math.max(1, delayTicks) * 50L, TimeUnit.MILLISECONDS));
    }

    public ScheduledTask runTaskTimerAsynchronously(Runnable task, long delayTicks, long periodTicks) {
        return new PaperScheduledTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduled -> task.run(), delayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS));
    }

    public ScheduledTask runTask(Entity entity, Runnable task) {
        var scheduled = entity.getScheduler().run(plugin, ignored -> task.run(), null);
        return scheduled == null ? null : new PaperScheduledTask(scheduled);
    }

    public ScheduledTask runTask(Location location, Runnable task) {
        return new PaperScheduledTask(Bukkit.getRegionScheduler().run(plugin, location, scheduled -> task.run()));
    }

    public void execute(Runnable task) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, task);
    }

    public void cancelTasks(Plugin owner) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(owner);
        Bukkit.getAsyncScheduler().cancelTasks(owner);
    }
}
