package me.zane.grassware.features.modules.render;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.HurtCamEvent;
import me.zane.grassware.event.events.InterpolateEvent;
import me.zane.grassware.event.events.OverlayEvent;
import me.zane.grassware.event.events.RenderLivingEntityEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;

public class NoRender extends Module {
    private final BooleanSetting hurtCam = register("HurtCam", false);
    private final BooleanSetting overlays = register("Overlays", false);
    private final BooleanSetting interp = register("Interpolation", false);
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
    @EventListener
    public void onInterpolate(InterpolateEvent event) {
        event.setCancelled(true);
    }
}
