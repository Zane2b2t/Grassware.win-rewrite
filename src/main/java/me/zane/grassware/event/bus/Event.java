package me.zane.grassware.event.bus;

public class Event {
    private boolean cancelled;

    public final boolean isCancelled() {
        return cancelled;
    }

    public final void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}