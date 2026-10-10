package me.frep.vulcan.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VulcanRegisterPlayerEvent extends Event implements Cancellable {

    private boolean cancelled;

    private final Player player;

    private static final HandlerList handlers = new HandlerList();

    public VulcanRegisterPlayerEvent(Player player2) {
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
}
