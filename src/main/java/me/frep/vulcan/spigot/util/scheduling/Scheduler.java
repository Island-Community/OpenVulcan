package me.frep.vulcan.spigot.util.scheduling;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public interface Scheduler {

    boolean isPrimaryThread();

    boolean isOwnedByCurrentThread(Entity entity);

    boolean isOwnedByCurrentThread(Location location);

    ScheduledTask runTask(Runnable task);

    ScheduledTask runTaskLater(Runnable task, long delayTicks);

    ScheduledTask runTaskTimer(Runnable task, long delayTicks, long periodTicks);

    ScheduledTask runTaskAsynchronously(Runnable task);

    ScheduledTask runTaskLaterAsynchronously(Runnable task, long delayTicks);

    ScheduledTask runTaskTimerAsynchronously(Runnable task, long delayTicks, long periodTicks);

    ScheduledTask runTask(Entity entity, Runnable task);

    ScheduledTask runTask(Location location, Runnable task);

    void execute(Runnable task);

    void cancelTasks(Plugin owner);

    default <T> Future<T> callSyncMethod(Callable<T> callable) {
        CompletableFuture<T> result = new CompletableFuture<>();
        execute(() -> {
            try {
                result.complete(callable.call());
            } catch (Throwable failure) {
                result.completeExceptionally(failure);
            }
        });
        return result;
    }
}
