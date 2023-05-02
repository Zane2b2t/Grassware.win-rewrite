package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class RenderItemEvent extends Event {
    public ItemStack stack;
    public EntityLivingBase entityLivingBase;

    public RenderItemEvent(ItemStack stack, EntityLivingBase entityLivingBase) {
        this.stack = stack;
        this.entityLivingBase = entityLivingBase;
    }

    public static class MainHand extends RenderItemEvent {
        public MainHand(ItemStack stack, EntityLivingBase entityLivingBase) {
            super(stack, entityLivingBase);
        }
    }

    public static class Offhand extends RenderItemEvent {
        public Offhand(ItemStack stack, EntityLivingBase entityLivingBase) {
            super(stack, entityLivingBase);
        }
    }
}