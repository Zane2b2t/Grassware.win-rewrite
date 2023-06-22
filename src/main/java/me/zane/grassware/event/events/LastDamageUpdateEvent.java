package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.entity.Entity;

public class LastDamageUpdateEvent extends Event {
    private final Entity entity;
    private final float amount;

    public LastDamageUpdateEvent(Entity entity, float amount) {
        this.entity = entity;
        this.amount = amount;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getAmount() {
        return amount;
    }
}

