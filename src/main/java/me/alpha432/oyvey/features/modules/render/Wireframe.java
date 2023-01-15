package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.impl.FloatSetting;
import me.alpha432.oyvey.features.setting.impl.BooleanSetting;
import me.alpha432.oyvey.features.setting.impl.ModeSetting;
import me.alpha432.oyvey.shader.impl.GradientShader;
import me.alpha432.oyvey.features.modules.client.ClickGui;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wireframe
        extends Module {
    private static Wireframe INSTANCE = new Wireframe();
    private final FloatSetting pOpacity = register("POpacity", 80.0f, 1.0f, 255.0f);
    private final FloatSetting cOpacity = register("COpacity", 80.0f, 1.0f, 255.0f);
    private final FloatSetting linewidth = register("PLineWidth", 1.0f, 0.0f, 6.0f);
    private final FloatSetting crystalLineWidth = register("CLineWidth", 1.0f, 0.0f, 6.0f);
  
    private final ModeSetting pMode = register("PMode", "SOLID", Arrays.asList("SOLID", "WIREFRAME"));
    private final ModeSetting cMode = register("CMode", "SOLID", Arrays.asList("SOLID", "WIREFRAME"));
  
    private final BooleanSetting players = register("Players", true);
    private final BooleanSetting playerModel = register("PlayerModel", false);
    private final BooleanSetting crystals = register("Crystals", true);
    private final BooleanSetting crystalModel = register("CrystalModel", false);

    public Wireframe() {
// zane has pro renders
    }

    public static Wireframe getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Wireframe();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
        }
    }
  
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
                opacity.getValue()
        );
        for (final Entity entity : mc.world.loadedEntityList) {
            if (!entity.equals(mc.player) && entity instanceof EntityPlayer || entity instanceof EntityEnderCrystal) {
                glPushMatrix();
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
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
                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glDisable(GL_BLEND);
                glPopMatrix();
            }
        }
        GradientShader.finish();
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }


}
