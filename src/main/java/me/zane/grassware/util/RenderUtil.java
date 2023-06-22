package me.zane.grassware.util;

import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.shader.impl.GradientShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;


public class RenderUtil implements MC {
    private static final ResourceLocation blank = new ResourceLocation("textures/blank.png");
    private static final Color transparent = new Color(0, 0, 0, 0);

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * mc.timer.renderPartialTicks;
    }
    public static void setAlphaLimit(float limit) {
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, (float) (limit * .01));
    }

   // public static void rounded(float x, float y, float width, float height, float radius, Color color) {
     //   setupDefault(color);
     //   glBegin(GL_TRIANGLE_FAN);
    //    for (int i = 1; i <= 4; i++) {
    //        corner(x, y, width, height, radius, i);
    //    }
        //releaseDefault();
  //  }


    public static Vec3d interpolateEntity(Entity entity) {
        double x;
        double y;
        double z;
        x = interpolateLastTickPos(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
        y = interpolateLastTickPos(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
        z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
        return new Vec3d(x, y, z);
    }

    public static void outlineShader(final BlockPos pos) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos);
        outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }


    public static void outlineShader(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(ClickGui.Instance.getColor());
        glBegin(GL_LINE_STRIP);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);

        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


    public static void boxShader(final BlockPos pos) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos);
        boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    public static void boxShader(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(ClickGui.Instance.getColor());
        glBegin(GL_TRIANGLE_STRIP);

        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


    public static void renderLine(Vec3d vec, Vec3d vec1) {
        final Vec3d offset = renderOffset();
        vec = vec.add(offset);
        vec1 = vec1.add(offset);
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glBegin(GL_LINES);

        glColor(ClickGui.Instance.getGradient()[0]);
        glVertex3d(vec.x, vec.y, vec.z);
        glColor(ClickGui.Instance.getGradient()[1]);
        glVertex3d(vec1.x, vec1.y, vec1.z);

        glEnd();

        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void renderGradientLine(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glBegin(GL_LINE_STRIP);

        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);

        glEnd();

        glBegin(GL_LINES);

        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glColor(transparent);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glColor(color);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glColor(transparent);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glColor(color);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glColor(transparent);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glColor(transparent);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);

        glEnd();

        glShadeModel(GL_FLAT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


    public static void boxShader(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);

        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glColor(transparent);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glColor(transparent);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glColor(color);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glColor(transparent);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glColor(color);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glColor(transparent);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glColor(color);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glColor(transparent);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);

        glEnd();
        glShadeModel(GL_FLAT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void rect(final float x, final float y, final float width, final float height, final Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
        glBegin(GL_QUADS);

        glVertex2f(x, y);
        glVertex2f(x, height);
        glVertex2f(width, height);
        glVertex2f(width, y);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void rectGuiTex(final float x, final float y, final float width, final float height, final Color color) {
        GradientShader.setup();
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(color);
        glBegin(GL_QUADS);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, height);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(width, height);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(width, y);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
        GradientShader.finish();
    }

    public static void texturedRect(final float x, final float y, final float width, final float height) {
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(Color.WHITE);
        glBegin(GL_QUADS);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, height);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(width, height);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(width, y);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void texturedOutline(final float x, final float y, final float width, final float height) {
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor(Color.WHITE);
        glBegin(GL_LINE_LOOP);

        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, height);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(width, height);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(width, y);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


    public static Vec3d renderOffset() {
        return new Vec3d(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
    }

    public static void bind(final ResourceLocation resourceLocation) {
        mc.getTextureManager().bindTexture(resourceLocation);
    }

    public static void bindBlank() {
        bind(blank);
    }

    public static void glColor(final Color color) {
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static void colorState(final Color color) {
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void boxShader(double placedPos, FloatSetting opacity) {
    }

    public static void boxShader(Double aDouble, double v) {
    }
}

