package me.zane.grassware.features.modules.movement;

import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;

public class ReverseStep extends Module {
private final FloatSetting fallSpeed = register("FallSpeed", 2.0f, 1.0f, 20.0f);

    @EventListener
    public void onTick(final TickEvent event) {
        if (mc.player.onGround && !mc.player.isInWater && !mc.player.isInLava && !mc.player.isOnLadder && !mc.gameSettings.keyBindJump.isKeyDown) {
            mc.player.motionY = -fallSpeed.getValue();
        }
    }
}
