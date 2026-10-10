package me.frep.vulcan.spigot.util.scheduling;

import java.util.Objects;
import me.frep.vulcan.spigot.lifecycle.PluginServices;

/**
 * Dispatches Bukkit work to the active server scheduler.
 */
public final class MainThreadExecutor {

    private MainThreadExecutor() {
    }

    public static void execute(Runnable task) {
        Scheduler scheduler = PluginServices.getScheduler();
        if (scheduler == null) {
            throw new IllegalStateException("Vulcan scheduler has not been initialized");
        }
        scheduler.runTask(Objects.requireNonNull(task, "task"));
    }
}
