package me.alpha432.oyvey.features.modules.render;


import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.NameplateEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.impl.FloatSetting;
import me.alpha432.oyvey.shader.impl.GradientShader;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class NameTags extends Module {
    private final FloatSetting scale = register("Scale", 1.5f, 0.1f, 10.0f);

    @EventListener
    public void onNameplate(final NameplateEvent event){
        if (event.entity instanceof EntityPlayer) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        final float scaleVal = this.scale.getValue() / 1000.0f;
        for (final EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player)){
                continue;
            }
            final Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
            glPushMatrix();
            glTranslated(vec.x, vec.y + entityPlayer.height * 1.25f, vec.z);
            glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            glRotatef((mc.getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);

            double distance = ((mc.getRenderViewEntity() == null) ? mc.player : mc.getRenderViewEntity()).getDistance(vec.x + mc.getRenderManager().viewerPosX, vec.y + mc.getRenderManager().viewerPosY, vec.z + mc.getRenderManager().viewerPosZ);
            double scale = 0.0018 + scaleVal * distance;

            glScaled(-scale, -scale, scale);
            glDisable(GL_DEPTH_TEST);

            final float health = (float) MathUtil.round(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), 1);
            final String text = entityPlayer.getName() + " " + health;

            GradientShader.setup(0.3f);
            RenderUtil.texturedRect(-OyVey.textManager.stringWidth(text) / 2.0f - 2.5f, -2.5f, OyVey.textManager.stringWidth(text) / 2.0f + 2.5f, 9.0f);
            GradientShader.finish();

            GradientShader.setup();
            RenderUtil.texturedOutline(-OyVey.textManager.stringWidth(text) / 2.0f - 2.5f, -2.5f, OyVey.textManager.stringWidth(text) / 2.0f + 2.5f, 9.0f);
            GradientShader.finish();

            OyVey.textManager.renderString(entityPlayer.getName(), -OyVey.textManager.stringWidth(text) / 2.0f, 0, Color.WHITE);
            OyVey.textManager.renderString(" " + health, (-OyVey.textManager.stringWidth(text) / 2.0f) + OyVey.textManager.stringWidth(entityPlayer.getName()), 0, healthColor(health));

            final ArrayList<ItemStack> stacks = new ArrayList<>();
            stacks.add(entityPlayer.getHeldItemMainhand());
            stacks.addAll(entityPlayer.inventory.armorInventory);
            stacks.add(entityPlayer.getHeldItemOffhand());
            float i = -48.0f;
            for (final ItemStack itemStack : stacks) {
                glPushMatrix();
                glClear(256);
                RenderHelper.enableStandardItemLighting();
                glEnable(GL_DEPTH_TEST);
                mc.getRenderItem().zLevel = -150.0f;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) i, -20);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) i, -20);
                mc.getRenderItem().zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
                glDisable(GL_DEPTH_TEST);
                glPopMatrix();
                i += 16.0f;
            }

            glEnable(GL_DEPTH_TEST);
            glPopMatrix();
            event.setCancelled(true);
        }
    }


    public Color healthColor(final float health) {
        final float g = (health * 7.083333333333333f) / 255.0f;
        final float r = ((36 - health) * 7.083333333333333f) / 255.0f;
        return new Color(r, g, 0, 1.0f);
    }
}
