package me.zane.grassware.util;

import me.zane.grassware.util.MC;
import net.minecraft.network.Packet;

public class PacketUtil implements MC {
    public static boolean noEvent;

    public static void invoke(Packet<?> packet) {
        if (mc.getConnection() != null) {
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
        }
    }

    public static void invoke(Packet<?> packet, int slot) {
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        if (mc.getConnection() != null) {
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
        }
        InventoryUtil.switchBack(currentItem);
    }

    public static void invokeNoEvent(Packet<?> packet) {
        if (mc.getConnection() != null) {
            noEvent = true;
            mc.getConnection().getNetworkManager().channel().writeAndFlush(packet);
            noEvent = false;
        }
    }


}