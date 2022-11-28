package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.HeldItemEvent;
import me.alpha432.oyvey.event.events.HurtCamEvent;
import me.alpha432.oyvey.event.events.OverlayEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.impl.BooleanSetting;

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
