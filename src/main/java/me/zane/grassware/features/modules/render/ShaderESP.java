package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.ArmorEvent;
import me.zane.grassware.event.events.FireEvent;
import me.zane.grassware.event.events.HeldItemEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.shader.impl.GradientShader;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import static org.lwjgl.opengl.GL11.*;

public class ShaderESP extends Module {
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting lineWidth = register("Line Width", 1.0f, 0f, 5.0f);

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        GradientShader.setup(
                ClickGui.Instance.step.getValue(),
                ClickGui.Instance.speed.getValue(),
                ClickGui.Instance.getGradient()[0],
                ClickGui.Instance.getGradient()[1],
                opacity.getValue()
        );
        for (final Entity entity : mc.world.loadedEntityList) {
            if (!entity.equals(mc.player) && entity instanceof EntityPlayer || entity instanceof EntityEnderCrystal) {
                glPushMatrix();
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glDisable(GL_BLEND);
                glPopMatrix();

                glPushMatrix();
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                glLineWidth(lineWidth.getValue());
                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glDisable(GL_BLEND);
                glPopMatrix();
            }
        }
        GradientShader.finish();
    }

    @EventListener
    public void onArmor(final ArmorEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onHeldItem(final HeldItemEvent event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onFire(final FireEvent event) {
        event.setCancelled(true);
    }
}
