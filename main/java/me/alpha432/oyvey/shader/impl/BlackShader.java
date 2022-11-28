package me.alpha432.oyvey.shader.impl;

import me.alpha432.oyvey.shader.ShaderUtil;
import me.alpha432.oyvey.util.MC;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class BlackShader implements MC {
    private final static ShaderUtil shader = new ShaderUtil("textures/shaders/black.frag");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setupUniforms() {
        shader.setUniformi("texture", 0);
    }


    public static void setup() {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.init();
        setupUniforms();

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.unload();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }
}