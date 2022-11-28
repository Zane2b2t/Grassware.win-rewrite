package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraft.util.MovementInput;

public class ItemInputUpdateEvent extends Event {
    public final MovementInput movementInput;

    public ItemInputUpdateEvent(final MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}