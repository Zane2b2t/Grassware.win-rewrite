package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DPreEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.util.EntityUtil;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class Aura extends Module {

    private final ModeSetting delayMode = register("Default", "Default", Arrays.asList("Default", "Custom"));
    private final IntSetting delay = register("Delay", 650, 1,800);
    private final BooleanSetting render = register("Render", true);
    private final IntSetting Range = register("Range", 6, 1, 6);

    private final Timer timer = new Timer();
    private float i = 0.0f;

    @EventListener
    public void onRender3DPre(final Render3DPreEvent event) {
        final EntityPlayer entityPlayer = EntityUtil.entityPlayer(this.Range.getValue());
        if (entityPlayer == null || !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            return;
        }
    if (render.getValue()) {
        final Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
        final Color color = ClickGui.Instance.getGradient()[0];
        final Color color2 = ClickGui.Instance.getGradient()[1];
        final Color top = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), 0);
        final float sin = ((float) Math.sin(i / 25.0f) / 2.0f);
        final float sin2 = ((float) Math.sin(i / 25.0f + 0.5f) / 2.0f);
        i++;
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_CULL_FACE);
        glBegin(GL_QUAD_STRIP);

        for (int i = 0; i <= 360; i++) {
            double x = ((Math.cos(i * Math.PI / 180F) * entityPlayer.width) + vec.x);
            double y = (vec.y + (entityPlayer.height / 2.0f)) + 0.1f;
            double z = ((Math.sin(i * Math.PI / 180F) * entityPlayer.width) + vec.z);
            RenderUtil.glColor(color);
            glVertex3d(x, y + (sin2 * entityPlayer.height), z);
            RenderUtil.glColor(top);
            glVertex3d(x, y + (sin * entityPlayer.height), z);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glShadeModel(GL_FLAT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }
    if (delayMode.getValue().equals("Default")) {
        if (!timer.passedMs(600)) {
            return;
        }
    }
    else if (delayMode.getValue().equals("Custom")) {
        if (!timer.passedMs(delay.getValue())) {
            return;
        }
    }
        mc.player.connection.sendPacket(new CPacketUseEntity(entityPlayer));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        timer.sync();
    }
    @Override
    public String getInfo() {
        return " [" + ChatFormatting.WHITE + "Closest" + ChatFormatting.RESET + "]";
    }
}
