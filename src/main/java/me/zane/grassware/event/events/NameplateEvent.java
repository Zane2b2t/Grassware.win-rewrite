package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.entity.Entity;

public class NameplateEvent extends Event {
    public final Entity entity;

    public NameplateEvent(final Entity entity){
        this.entity = entity;
    }
}
