package me.frep.vulcan.spigot.util.scheduling;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState;
import org.bukkit.plugin.Plugin;

public class PaperScheduledTask implements ScheduledTask {

    private final io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask;

    public PaperScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask scheduledTask2) {
        this.scheduledTask = scheduledTask2;
    }

    public void cancel() {
        this.scheduledTask.cancel();
    }

    public boolean isCancelled() {
        return this.scheduledTask.isCancelled();
    }

    public Plugin getOwner() {
        return this.scheduledTask.getOwningPlugin();
    }

    public boolean isRunning() {
        ExecutionState executionState = this.scheduledTask.getExecutionState();
        return executionState == ExecutionState.RUNNING || executionState == ExecutionState.CANCELLED_RUNNING;
    }

    public boolean isRepeating() {
        return this.scheduledTask.isRepeatingTask();
    }
}
