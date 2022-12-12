
package me.zane.grassware.features.modules.render;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.shader.impl.GradientShader;


import java.util.HashMap;
import java.util.Map;

public class GlintModify extends Module {
    
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

    
