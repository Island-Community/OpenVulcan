package me.frep.vulcan.spigot.util.scheduling;

import org.bukkit.plugin.Plugin;

public interface ScheduledTask {

    void cancel();

    boolean isCancelled();

    Plugin getOwner();

    boolean isRunning();

    boolean isRepeating();
}
