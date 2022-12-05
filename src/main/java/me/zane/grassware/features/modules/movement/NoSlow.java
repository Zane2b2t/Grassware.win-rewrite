package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.ItemInputUpdateEvent;
import me.zane.grassware.features.modules.Module;
import net.minecraft.util.MovementInput;

public class NoSlow extends Module {

    @EventListener
    public void onItemInputUpdate(final ItemInputUpdateEvent event) {
        if (slowed()) {
            final MovementInput movementInput = event.movementInput;
            movementInput.moveForward /= 0.2f;
            movementInput.moveStrafe /= 0.2f;
        }
    }

    private boolean slowed() {
        return mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isElytraFlying();
    }
}
