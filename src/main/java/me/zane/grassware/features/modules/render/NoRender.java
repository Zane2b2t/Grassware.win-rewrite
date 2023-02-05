package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.HurtCamEvent;
import me.zane.grassware.event.events.OverlayEvent;
import me.zane.grassware.event.events.RenderLivingEntityEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;

import net.minecraftforge.fml.common.eventhandler.Event;


public class NoRender extends Module {
    private final BooleanSetting hurtCam = register("HurtCam", false);
    private final BooleanSetting overlays = register("Overlays", false);
    private final BooleanSetting strictLimbs = register("StrictLimbs", false);

    @EventListener
    public void onHurCam(final HurtCamEvent event) {
        if (hurtCam.getValue()) {
            event.setCancelled(true);
        }
    }
    
    @EventListener
    private void onRenderLivingEntity(final RenderLivingEntityEvent event) {
        if (strictLimbs.getValue()) {
            event.getEntityLivingBase().limbSwing = 0;
            event.getEntityLivingBase().limbSwingAmount = 0;
            event.getEntityLivingBase().prevLimbSwingAmount = 0;
            event.getEntityLivingBase().swingProgress = 0;
            
        }
    }

    @EventListener
    public void onOverlay(final OverlayEvent event) {
        if (overlays.getValue()) {
            event.setCancelled(true);
        }
    }
}
