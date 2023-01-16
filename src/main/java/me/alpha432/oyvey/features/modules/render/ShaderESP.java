package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.ArmorEvent;
import me.alpha432.oyvey.event.events.FireEvent;
import me.alpha432.oyvey.event.events.RenderItemInFirstPersonEvent;
import me.alpha432.oyvey.event.events.HeldItemEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.impl.FloatSetting;
import me.alpha432.oyvey.features.setting.impl.BooleanSetting;
import me.alpha432.oyvey.shader.impl.GradientShader;
import me.alpha432.oyvey.mixin.IEntityRenderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

import static me.alpha432.oyvey.util.RenderUtil.glColor;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class ShaderESP extends Module {
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting lineWidth = register("Line Width", 1.0f, 0.1f, 5.0f);
   // private final BooleanSetting items = register("Items", true);

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
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand();

        GradientShader.finish();
    }

    
    @EventListener
    public void renderItemInFirstPerson(RenderItemInFirstPersonEvent event) {

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
