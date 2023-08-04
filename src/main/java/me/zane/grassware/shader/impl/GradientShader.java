package me.zane.grassware.shader.impl;

import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.shader.ShaderUtil;
import me.zane.grassware.util.MC;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class GradientShader implements MC {
    private final static ShaderUtil shader = new ShaderUtil("/assets/minecraft/textures/shaders/gradient.frag");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setupUniforms(final float step, final float speed, final Color color, final Color color2, final Color color3, final Color color4, final float opacity) {
        shader.setUniformi("texture", 0);
        shader.setUniformf("rgb", color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        shader.setUniformf("rgb1", color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f);
        shader.setUniformf("rgb2", color3.getRed() / 255.0f, color3.getGreen() / 255.0f, color3.getBlue() / 255.0f);
        shader.setUniformf("rgb3", color4.getRed() / 255.0f, color4.getGreen() / 255.0f, color4.getBlue() / 255.0f);
        shader.setUniformf("step", 300 * step);
        shader.setUniformf("offset", 0);
        shader.setUniformf("mix", opacity);
    }

    public static void setup() {
        setup(ClickGui.Instance.step.getValue(), ClickGui.Instance.speed.getValue(), ClickGui.Instance.getGradient()[0], ClickGui.Instance.getGradient()[1], ClickGui.Instance.getGradient()[2], ClickGui.Instance.getGradient()[3]);
    }

    public static void setup(final float opacity) {
        setup(ClickGui.Instance.step.getValue(), ClickGui.Instance.speed.getValue(), ClickGui.Instance.getGradient()[0], ClickGui.Instance.getGradient()[1], ClickGui.Instance.getGradient()[2], ClickGui.Instance.getGradient()[3], opacity);
    }


    public static void setup(final float step, final float speed, final Color color, final Color color2, final Color color3, final Color color4) {
        setup(step, speed, color, color2, color3, color4, 1.0f);
    }

    public static void setup(final float step, final float speed, final Color color, final Color color2, final Color color3, final Color color4, final float opacity) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.init();
        setupUniforms(step, speed, color, color2, color3, color4, opacity);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.unload();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
        glEnable(GL_BLEND);
    }
}
