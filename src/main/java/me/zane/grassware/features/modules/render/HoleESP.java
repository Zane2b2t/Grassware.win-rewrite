package me.zane.grassware.features.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DPreEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.manager.EventManager;
import me.zane.grassware.manager.HoleManager;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HoleESP extends Module {
    private final IntSetting range = register("Range", 6, 1, 20);
    private final IntSetting speed = register("Speed", 60, 1, 200);
    private final FloatSetting height = register("Height", 1.0f, 0.0f, 3.0f);
    private final FloatSetting opacity = register("Opacity", 0.7f, 0.0f, 1.0f);
    private final FloatSetting floorOpacity = register("FloorOpacity", 0.5f, 0.0f, 1.0f);
    private final ModeSetting mode = register("Mode", "Fade", Arrays.asList("Fade", "Gradient", "FadeGradient"));
    private final BooleanSetting floor = register("Floor", false);

    private final ArrayList<Hole> renderHoles = new ArrayList<>();
    private final ICamera camera = new Frustum();

    private boolean differentRenderType(final HoleManager.HolePos pos) {
        return GrassWare.holeManager.getHoles().stream()
                .filter(h -> h.getPos().equals(pos.getPos()))
                .anyMatch(h -> !h.getHoleType().equals(pos.getHoleType()));
    }

    private boolean holesContains(final HoleManager.HolePos pos) {
        return renderHoles.stream().anyMatch(h -> h.holePos.getPos().equals(pos.getPos()));
    }

    @EventListener
    public void onRender3D(final Render3DPreEvent event) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX,
                mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        GrassWare.threadManager.invokeThread(() -> GrassWare.holeManager.loadHoles(range.getValue()));

        for (HoleManager.HolePos holePos : GrassWare.holeManager.getHoles()) {
            final boolean diff = differentRenderType(holePos);
            if (!holesContains(holePos) || diff) {
                renderHoles.add(new Hole(holePos));
            }
        }

        renderHoles.removeIf(h -> !GrassWare.holeManager.holeManagerContains(h.holePos.getPos()) && h.size <= 0.05f);

        for (Hole h : new ArrayList<>(renderHoles)) {
            h.out = !GrassWare.holeManager.holeManagerContains(h.holePos.getPos());
            h.render();
        }
    }

    public class Hole {
        public final HoleManager.HolePos holePos;
        public boolean out;
        public long sys;
        public float size;

        public Hole(final HoleManager.HolePos holePos) {
            this.holePos = holePos;
            this.out = false;
            this.sys = System.currentTimeMillis();
            this.size = 0.0f;
        }

        public void render() {
            size = MathUtil.lerp(size, out ? 0.0f : 1.0f, 0.02f * EventManager.deltaTime * speed.getValue() / 100.0f);

            final AxisAlignedBB raw = new AxisAlignedBB(holePos.getPos());
            if (!camera.isBoundingBoxInFrustum(raw.grow(2.0))) return;

            double x1 = raw.minX, x2 = raw.maxX;
            double z1 = raw.minZ, z2 = raw.maxZ;
            if (holePos.isDouble()) {
                if (holePos.isWestDouble()) x1 -= 1.0;
                else z1 -= 1.0;
            }

            final double shaderBottom = raw.minY;
            final double shaderTop = raw.minY + size * height.getValue();
            final double legacyTop = raw.maxY - 1.0 + size;

            // --- Fade ---
            if (mode.getValue().equals("Fade")) {
                final int index = holePos.isBedrock() ? 1 : 0;
                final Color color = ClickGui.Instance.getGradient()[index];

                RenderUtil.boxShader(x1, raw.minY, z1, x2, legacyTop, z2, color);
                RenderUtil.renderGradientLine(x1, raw.minY, z1, x2, legacyTop, z2, color);
            }


// --- Gradient ---
            else if (mode.getValue().equals("Gradient")) {
                GradientShader.setup(opacity.getValue());

                // Animate x2 and z2 from x1/z1 toward their max values
                final double animX2 = x1 * (1.0f - size) + x2 * size;
                final double animZ2 = z1 * (1.0f - size) + z2 * size;

                RenderUtil.boxShader(x1, shaderBottom, z1, animX2, shaderTop, animZ2);
                RenderUtil.outlineFadeGradientShader(x1, shaderBottom, z1, animX2, shaderTop, animZ2);

                GradientShader.finish();
            }


            else if (mode.getValue().equals("FadeGradient")) {
                GradientShader.setup(opacity.getValue());

                // Animate x2 and z2 from x1/z1 toward their max values
                final double animX2 = x1 * (1.0f - size) + x2 * size;
                final double animZ2 = z1 * (1.0f - size) + z2 * size;

                RenderUtil.boxFadeGradientShader(x1, shaderBottom, z1, animX2, shaderTop, animZ2);
                RenderUtil.outlineFadeGradientShader(x1, shaderBottom, z1, animX2, shaderTop, animZ2);

                GradientShader.finish();

                if (floor.getValue()) {
                    GradientShader.setup(floorOpacity.getValue());
                    // Floor uses animated coordinates too for consistency
                    RenderUtil.boxShader(x1, raw.minY, z1, animX2, raw.minY + 0.001, animZ2);
                    GradientShader.finish();
                }
            }
        }
    }
}
