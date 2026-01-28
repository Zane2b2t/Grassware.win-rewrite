package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.Render3DPostEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.mixin.mixins.IEntityRenderer;
import me.zane.grassware.shader.impl.GradientShader;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Hand extends Module {
    public static Hand INSTANCE = new Hand();
    public static boolean rendering;

    public final BooleanSetting shader = register("Shader", true);
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);

    @EventListener
    public void onRender3D(Render3DPostEvent event) {

        if (mc.gameSettings.thirdPersonView != 0) return;
        if (rendering) return;
        if (!shader.getValue()) return;

        rendering = true;

        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        try {
            GlStateManager.enableBlend();
            GlStateManager.disableCull();
            GlStateManager.depthMask(true);

            GradientShader.setup(opacity.getValue());

            ((IEntityRenderer) mc.entityRenderer)
                    .invokeRenderHand(mc.getRenderPartialTicks(), 2);

            GradientShader.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GL11.glPopAttrib();
        GlStateManager.popMatrix();
        rendering = false;
    }
}
