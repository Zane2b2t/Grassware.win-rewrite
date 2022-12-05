package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.HurtCamEvent;
import me.zane.grassware.event.events.OverlayEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;

public class NoRender extends Module {
    private final BooleanSetting hurtCam = register("HurtCam", false);
    private final BooleanSetting overlays = register("Overlays", false);

    @EventListener
    public void onHurCam(final HurtCamEvent event){
        if (hurtCam.getValue()){
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onOverlay(final OverlayEvent event){
        if (overlays.getValue()) {
            event.setCancelled(true);
        }
    }
}
