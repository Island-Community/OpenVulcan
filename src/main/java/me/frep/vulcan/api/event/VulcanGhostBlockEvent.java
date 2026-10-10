package me.frep.vulcan.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VulcanGhostBlockEvent extends Event implements Cancellable {

    private final Player player;

    private boolean cancelled;

    private final long timestamp = System.currentTimeMillis();

    private static final HandlerList handlers = new HandlerList();

    public VulcanGhostBlockEvent(Player player2) {
        super(true);
        this.player = player2;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled2) {
        this.cancelled = cancelled2;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
