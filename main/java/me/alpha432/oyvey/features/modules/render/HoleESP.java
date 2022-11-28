package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.impl.IntSetting;
import me.alpha432.oyvey.manager.EventManager;
import me.alpha432.oyvey.manager.HoleManager;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class HoleESP extends Module {
    private final IntSetting range = register("Range", 5, 1, 20);
    private final IntSetting speed = register("Speed", 50, 1, 200);
    private final ArrayList<Hole> renderHoles = new ArrayList<>();
    private final ICamera camera = new Frustum();

    private boolean differentRenderType(final HoleManager.HolePos pos) {
        return OyVey.holeManager.getHoles().stream().filter(holePos -> holePos.getPos().equals(pos.getPos())).anyMatch(holePos -> !holePos.getHoleType().equals(pos.getHoleType()));
    }

    private boolean holesContains(final HoleManager.HolePos pos) {
        return renderHoles.stream().anyMatch(renderHole -> renderHole.holePos.getPos().equals(pos.getPos()));
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        OyVey.threadManager.invokeThread(() -> {
            OyVey.holeManager.loadHoles(range.getValue());
            invokeInfo(OyVey.holeManager.time);
        });

        for (HoleManager.HolePos holePos : OyVey.holeManager.getHoles()) {
            final boolean diff = differentRenderType(holePos);
            if (!holesContains(holePos) || diff) {
                Hole hole1 = new Hole(holePos);
                renderHoles.add(hole1);
            }
        }

        new ArrayList<>(renderHoles).forEach(hole -> {
            if (!OyVey.holeManager.holeManagerContains(hole.holePos.getPos()) || differentRenderType(hole.holePos)) {
                hole.out = true;
                if (hole.size <= 0.1f) {
                    renderHoles.remove(hole);
                    return;
                }
            }
            hole.render();
        });
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
            final int index = holePos.isBedrock() ? 1 : 0;
            final Color color = ClickGui.Instance.getGradient()[index];
            final AxisAlignedBB bb = new AxisAlignedBB(holePos.getPos());
            if (!camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                return;
            }
            if (holePos.isDouble()) {
                if (holePos.isWestDouble()) {
                    RenderUtil.boxShader(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + size, bb.maxZ, color);
                    RenderUtil.renderGradientLine(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + size, bb.maxZ, color);
                } else {
                    RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + size, bb.maxZ, color);
                    RenderUtil.renderGradientLine(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + size, bb.maxZ, color);
                }
            } else {
                RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + size, bb.maxZ, color);
                RenderUtil.renderGradientLine(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + size,  bb.maxZ, color);
            }
        }
    }
}
