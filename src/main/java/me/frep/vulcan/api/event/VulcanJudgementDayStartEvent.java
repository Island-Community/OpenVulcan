package me.frep.vulcan.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VulcanJudgementDayStartEvent extends Event implements Cancellable {

    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    public VulcanJudgementDayStartEvent() {
        super(true);
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
}
