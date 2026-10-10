package me.frep.vulcan.spigot.tracker.confirmation;

import java.util.ArrayList;
import java.util.List;

public final class PendingConfirmation {

    private final int id;

    private final long sentAtMillis;

    private final List<Runnable> callbacks = new ArrayList<>();

    public PendingConfirmation(int id, long sentAtMillis) {
        this.id = id;
        this.sentAtMillis = sentAtMillis;
    }

    public boolean addCallback(Runnable callback) {
        return callbacks.add(callback);
    }

    public void complete() {
        callbacks.forEach(Runnable::run);
        callbacks.clear();
    }

    public int getId() {
        return id;
    }

    public long getSentAtMillis() {
        return sentAtMillis;
    }

    public List<Runnable> getCallbacks() {
        return callbacks;
    }
}
