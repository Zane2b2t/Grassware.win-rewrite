package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.shader.impl.GradientShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ItemOverlay extends Module {
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.0f, 1.0f);
    public void renderOverlay(Minecraft mc, ItemStack stack, int x, int y) {
        if (stack != null && stack.getItem() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(16, 16, 0);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            GradientShader.setup(opacity.getValue());
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            GradientShader.finish();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
    @EventListener
    public void onRender2D(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack stack = mc.player.getHeldItemMainhand();
            ScaledResolution resolution = new ScaledResolution(mc);
            int x = resolution.getScaledWidth() / 2;
            int y = resolution.getScaledHeight() - 16;
            ItemOverlay itemOverlay = new ItemOverlay();
            itemOverlay.renderOverlay(mc, stack, x, y);
        }
    }


}
