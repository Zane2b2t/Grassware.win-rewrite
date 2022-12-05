package me.zane.grassware.features.modules.client;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.*;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.manager.EventManager;
import me.zane.grassware.shader.impl.BlackShader;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.event.events.Render2DEvent;
import me.zane.grassware.event.events.RenderHotbarEvent;
import me.zane.grassware.event.events.RenderPotionEffectsEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class Hud extends Module {
    private final ArrayList<Module> modules = new ArrayList<>();
    private final BooleanSetting watermark = register("Watermark", false);
    private final BooleanSetting welcomer = register("Welcomer", false);
    private final BooleanSetting moduleList = register("Module List", false);
    private final BooleanSetting customHotbar = register("Custom Hotbar", false);

    @EventListener
    public void onRender2D(final Render2DEvent event) {
        if (watermark.getValue()) {
            registerHudText(GrassWare.MODNAME + " " + GrassWare.MODVER, 0.0f, 0.0f, false);
        }
        if (welcomer.getValue()) {
            final String text = "Welcome to " + GrassWare.MODNAME + " " + GrassWare.MODVER + ", " + mc.player.getName() + "!";
            registerHudText(text, event.scaledResolution.getScaledWidth() / 2.0f - GrassWare.textManager.stringWidth(text) / 2.0f, 0.0f, false);
        }
        if (moduleList.getValue()) {
            GrassWare.moduleManager.modules.stream().filter(module -> module.isEnabled() && !modules.contains(module)).forEach(modules::add);
            modules.sort(Comparator.comparing(Module::totalStringWidth));
            float deltaY = 0.0f;
            for (final Module module : new ArrayList<>(modules)) {
                if (!module.drawn.getValue()) {
                    continue;
                }
                module.anim = MathUtil.lerp(module.anim, module.isEnabled() ? 1.0f : 0.0f, 0.005f * EventManager.deltaTime);
                final float x = event.scaledResolution.getScaledWidth() + (module.anim * (module.stringWidth() + module.infoWidth()));
                if (!module.isEnabled() && module.anim < 0.05f) {
                    modules.remove(module);
                }
                registerHudText(module.getName(), x, deltaY, false);
                registerHudText(module.getInfo(), x - module.stringWidth(), deltaY, true);
                deltaY += GrassWare.textManager.stringHeight() * module.anim;
            }
        }
    }


    private void registerHudText(final String text, final float x, final float y, final boolean gray) {
        if (gray) {
            GrassWare.textManager.renderString(text, x, y, Color.GRAY);
            BlackShader.setup();
            GrassWare.textManager.renderStringShadowOnly(text, x, y);
            BlackShader.finish();
            return;
        }
        if (!ClickGui.Instance.mode.getValue().equals("Static")) {
            BlackShader.setup();
            GrassWare.textManager.renderStringShadowOnly(text, x, y);
            BlackShader.finish();
        } else {
            GrassWare.textManager.renderStringShadowOnly(text, x, y);
        }

        GradientShader.setup();
        GrassWare.textManager.renderStringNoShadow(text, x, y, ClickGui.Instance.getColor());
        GradientShader.finish();
    }

    @EventListener
    public void onRenderHotbar(final RenderHotbarEvent event) {
        if (!customHotbar.getValue()) {
            return;
        }
        final ScaledResolution scaledResolution = event.scaledResolution;
        final float centerX = scaledResolution.getScaledWidth() / 2.0f;
        final float height = scaledResolution.getScaledHeight();
        GradientShader.setup();
        float x = -81.0f;
        for (int i = 0; i < 9; i++) {
            RenderUtil.texturedOutline(centerX + x, height - 18.0f, centerX + x + 18.0f, height);
            x += 18.0f;
        }
        GradientShader.finish();

        x = -81.0f;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.currentItem == i) {
                RenderUtil.rect(centerX + x + 1, height - 17.0f, centerX + x + 17.0f, height - 1, new Color(0, 0, 0, 150));
            }
            final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            glPushMatrix();
            glClear(256);
            RenderHelper.enableStandardItemLighting();
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            mc.getRenderItem().zLevel = -150.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (centerX + x + 1.0f), (int) (height - 17.0f));
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) (centerX + x + 1.0f), (int) (height - 17.0f));
            mc.getRenderItem().zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            glDisable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            glPopMatrix();
            x += 18.0f;
        }
        event.setCancelled(true);
    }

    @EventListener
    public void onRenderPotionEffects(final RenderPotionEffectsEvent event) {
        event.setCancelled(true);
    }
}
