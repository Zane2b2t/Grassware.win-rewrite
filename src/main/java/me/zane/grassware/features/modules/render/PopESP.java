package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.*;
import me.zane.grassware.event.events.*;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.shader.impl.GradientShader;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class PopESP extends Module {
    private final FloatSetting lineWidth = register("Line Width", 1.0f, 0f, 5.0f);
    private final HashMap<EntityPlayer, Long> playerList = new HashMap<>();

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        for (final Map.Entry<EntityPlayer, Long> entry : new HashMap<>(playerList).entrySet()) {
            final float alpha = (System.currentTimeMillis() - entry.getValue()) / 1000.0f;
            if (alpha > 1.0f) {
                playerList.remove(entry.getKey());
                continue;
            }
            GradientShader.setup(
                    ClickGui.Instance.step.getValue(),
                    ClickGui.Instance.speed.getValue(),
                    ClickGui.Instance.getGradient()[0],
                    ClickGui.Instance.getGradient()[1],
                    Math.max(0.0f, 1.0f - alpha)
            );
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            mc.getRenderManager().renderEntityStatic(entry.getKey(), mc.getRenderPartialTicks(), false);
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
            mc.getRenderManager().renderEntityStatic(entry.getKey(), mc.getRenderPartialTicks(), false);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
            GradientShader.finish();
        }
    }

    @EventListener
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = event.getPacket();
            final Entity entity = packet.getEntity(mc.world);
            if (entity instanceof EntityPlayer && packet.getOpCode() == 35) {
                invokeEntity((EntityPlayer) entity);
            }
        }
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

    @Override
    public void onEnable() {
        invokeEntity(mc.player);
    }

    private void invokeEntity(final EntityPlayer entityPlayer) {
        if (entityPlayer.equals(mc.player)) {
            return;
        }
        final EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        player.copyLocationAndAnglesFrom(entityPlayer);

        player.prevRotationYaw = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYawHead;
        player.prevRotationPitch = player.rotationPitch;

        player.entityId = -1;
        playerList.put(player, System.currentTimeMillis());
    }
}
