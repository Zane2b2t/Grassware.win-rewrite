package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.util.MovementInput;

public class ItemInputUpdateEvent extends Event {
    public final MovementInput movementInput;

    public ItemInputUpdateEvent(final MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}