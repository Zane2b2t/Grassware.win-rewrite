package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraft.entity.Entity;

public class NameplateEvent extends Event {
    public final Entity entity;

    public NameplateEvent(final Entity entity){
        this.entity = entity;
    }
}
