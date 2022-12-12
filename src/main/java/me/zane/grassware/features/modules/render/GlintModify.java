
package me.zane.grassware.features.modules.render;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.*;



import java.util.HashMap;
import java.util.Map;

public class GlintModify extends Module {
    
     @EventListener
    public void onRender3D(final Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }

            GradientShader.setup(
                    ClickGui.Instance.step.getValue(),
                    ClickGui.Instance.speed.getValue(),
                    ClickGui.Instance.getGradient()[0],
                    ClickGui.Instance.getGradient()[1],
                    Math.max(0.0f, 1.0f - alpha)
            );
          
        }
    }
}
    
