//this module is china. RenderGlientOnce always works even when module is disabled. also shit no worky
package me.zane.grassware.features.modules.render;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.bus.Event;
import me.zane.grassware.event.events.Render2DEvent;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.mixin.mixins.IEntityRenderer;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.modules.Module;

public class Hand extends Module {
    public static Hand INSTANCE = new Hand();
    public static boolean rendering;

    public final BooleanSetting renderGlintOnce = register("RenderGlintOnce", true);
    public final IntSetting alpha = register("GlintOpacity", 255, 0, 255);
    public final BooleanSetting shader = register("Shader", true);
    public final BooleanSetting twoD = register("2DEvent", false);
    public final FloatSetting opacity = register("ShaderOpacity", 90.0F, 0.0F, 255.0F);

@EventListener
     public void onRender3D(final Render3DEvent event) {
       if (mc.gameSettings.thirdPersonView != 0) {
        return;
      }

    if (shader.getValue()) {
        GradientShader.setup(opacity.getValue());
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
        GradientShader.finish(); //idk how to add outline to items GRRR AGHHADHS
    }
}
    @EventListener
    public void onRender2D(final Render2DEvent event) {
        if (shader.getValue() && twoD.getValue()) {
            GradientShader.setup(opacity.getValue());
            ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
            GradientShader.finish();
        }
    }

}