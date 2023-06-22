package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

import net.minecraft.item.ItemStack;

public class RenderToolTipEvent extends Event {
    private final ItemStack itemStack;
    private int x, y;

    public RenderToolTipEvent(ItemStack itemStack, int x, int y) {
        this.itemStack = itemStack;
        this.x = x;
        this.y = y;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
