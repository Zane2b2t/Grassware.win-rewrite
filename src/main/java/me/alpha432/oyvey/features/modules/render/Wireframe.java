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

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }


}
