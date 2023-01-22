package me.zane.grassware.shader.impl;

import me.zane.grassware.GrassWare;
import me.zane.grassware.mixin.mixins.IEntityRenderer;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.shader.impl.GradientShader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.opengl.Display;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class FramebufferShader implements MC {

    protected static int lastScale;
    protected static int lastScaleWidth;
    protected static int lastScaleHeight;
    private static Framebuffer framebuffer;
  //  protected float red, green, blue, alpha = 1F;
    protected float radius = 2F;
  //  protected float quality = 1F;

    private boolean entityShadows;

    public FramebufferShader(String fragmentShader) {
        super(fragmentShader);
    }

    public void startDraw(float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        framebuffer = setupFrameBuffer(framebuffer);
        framebuffer.bindFramebuffer(true);
        entityShadows = GrassWare.mc.gameSettings.entityShadows;
        GrassWare.mc.gameSettings.entityShadows = false;
        ((IEntityRenderer) GrassWare.mc.entityRenderer).invokeSetupCameraTransform(partialTicks, 0);
              GradientShader.setup(
                ClickGui.Instance.step.getValue(),
                ClickGui.Instance.speed.getValue(),
                ClickGui.Instance.getGradient()[0],
                ClickGui.Instance.getGradient()[1],
                opacity.getValue()
        );
        GradientShader.finish();
    }

    public void stopDraw(Color color, float radius, float quality) {
        GrassWare.mc.gameSettings.entityShadows = entityShadows;
        GlStateManager.enableBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GrassWare.mc.getFramebuffer().bindFramebuffer(true);

         speed = ClickGui.Instance.spped.getValue();
         step = ClickGui.Instance.step.getValue();
         gradient1 = ClickGui.Instance.getGradient()[0];
         gradient2 = ClickGui.Instance.getGradient()[1];
        alpha = opacity.getValue();
         this.radius = radius;                                                                         // blue = color.getBlue() / 255F;
                                                                                   // alpha = color.getAlpha() / 255F;
        
                                                                                   // this.quality = quality;

        GrassWare.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();

        startShader();
        GrassWare.mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();

        GrassWare.mc.entityRenderer.disableLightmap();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (Display.isActive() || Display.isVisible()) {
            if (frameBuffer != null) {
                frameBuffer.framebufferClear();
                ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
                int factor = scale.getScaleFactor();
                int factor2 = scale.getScaledWidth();
                int factor3 = scale.getScaledHeight();
                if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3) {
                    frameBuffer.deleteFramebuffer();
                    frameBuffer = new Framebuffer(Ruby.mc.displayWidth, Ruby.mc.displayHeight, true);
                    frameBuffer.framebufferClear();
                }
                lastScale = factor;
                lastScaleWidth = factor2;
                lastScaleHeight = factor3;
            } else {
                frameBuffer = new Framebuffer(GrassWare.mc.displayWidth, GrassWare.mc.displayHeight, true);
            }
        } else {
            if (frameBuffer == null) {
                frameBuffer = new Framebuffer(GrassWare.mc.displayWidth, GrassWare.mc.displayHeight, true);
            }
        }

        return frameBuffer;
    }

    public void drawFramebuffer(Framebuffer framebuffer) {
        ScaledResolution scaledResolution = new ScaledResolution(Ruby.mc);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, 0);
        glTexCoord2d(0, 0);
        glVertex2d(0, scaledResolution.getScaledHeight());
        glTexCoord2d(1, 0);
        glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        glTexCoord2d(1, 1);
        glVertex2d(scaledResolution.getScaledWidth(), 0);
        glEnd();
        glUseProgram(0);
    }

}
