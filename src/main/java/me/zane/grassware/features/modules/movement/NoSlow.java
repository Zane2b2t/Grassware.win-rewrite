package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.ItemInputUpdateEvent;
import me.zane.grassware.features.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.MovementInput;

public class NoSlow extends Module {
       private final BooleanSetting strict = register("Strict", false);
       private final BooleanSetting toobee = register("2b2t", false);

    @EventListener
    public void onItemInputUpdate(final ItemInputUpdateEvent event) {
        if (slowed()) {
            final MovementInput movementInput = event.movementInput;
            movementInput.moveForward /= 0.2f;
            movementInput.moveStrafe /= 0.2f;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && toobee.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    private boolean slowed() {
        return mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isElytraFlying();
    }
}
