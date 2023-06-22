package me.zane.grassware.shader.impl;

import me.zane.grassware.shader.ShaderUtil;
import me.zane.grassware.util.MC;
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glUniform1;

public class ShadowGradientShader implements MC {
    private final static ShaderUtil shadowShader = new ShaderUtil("/assets/minecraft/textures/shaders/shadowgradient.frag");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static Framebuffer buffer = new Framebuffer(1, 1, false);

    public static void invokeShadow(){
        buffer = RenderUtil.createFrameBuffer(buffer);
        buffer.framebufferClear();
        buffer.bindFramebuffer(true);
    }

    public static void releaseShadow(int radius, int offset, float intensity, float step, float speed, Color color, Color color2){
        buffer.unbindFramebuffer();
        render(buffer.framebufferTexture, radius, offset, intensity, step, speed, color, color2);
    }

    private static void render(int sourceTexture, int radius, int offset, float intensity, float step, float speed, Color color, Color color2) {
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(516, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(MathUtil.gaussian(i, radius));
        }
        weightBuffer.rewind();

        RenderUtil.setAlphaLimit(0.0F);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shadowShader.attachShader();
        setupUniforms(radius, offset, 0, weightBuffer, intensity, step, speed, color, color2);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        ShaderUtil.screenTex();
        shadowShader.releaseShader();
        framebuffer.unbindFramebuffer();


        mc.getFramebuffer().bindFramebuffer(true);

        shadowShader.attachShader();
        setupUniforms(radius, 0, offset, weightBuffer, intensity, step, speed, color, color2);
        glActiveTexture(GL_TEXTURE16);
        glBindTexture(GL_TEXTURE_2D, sourceTexture);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        ShaderUtil.screenTex();
        shadowShader.releaseShader();

        glAlphaFunc(516, 0.1f);
        glEnable(GL_ALPHA_TEST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights,float intensity, float step, float speed, Color color, Color color2) {
        shadowShader.setUniformi("inTexture", 0);
        shadowShader.setUniformi("textureToCheck", 16);
        shadowShader.setUniformf("radius", radius);
        shadowShader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shadowShader.setUniformf("direction", directionX, directionY);
        shadowShader.setUniformf("intensity", intensity);
        shadowShader.setUniformf("rgb", color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        shadowShader.setUniformf("rgb1", color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f);
        shadowShader.setUniformf("step", 300 * step);
        shadowShader.setUniformf("o", (float) ((((double) System.currentTimeMillis() * (double) speed) % (mc.displayWidth * mc.displayHeight)) / 10.0f));
        glUniform1(shadowShader.getUniform("weights"), weights);
    }
}
