package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;


import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderHeldItemEvent extends Event {
    private final EnumHandSide side;

    public RenderHeldItemEvent(EnumHandSide enumHandSide) {
        this.side = enumHandSide;
    }

    public EnumHandSide getSide() {
        return side;
    }

    public static class Pre extends RenderHeldItemEvent {
        public Pre(EnumHandSide enumHandSide) {
            super(enumHandSide);
        }
    }

    public static class Post extends RenderHeldItemEvent {
        public Post(EnumHandSide enumHandSide) {
            super(enumHandSide);
        }
    }
}
