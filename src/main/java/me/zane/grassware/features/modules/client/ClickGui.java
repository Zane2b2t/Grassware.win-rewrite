package me.zane.grassware.features.modules.client;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.gui.GrassWareGui;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;


public class ClickGui extends Module {
    public static ClickGui Instance;

    private final Random random = new Random();
    public final ModeSetting mode = register("Mode", "Gradient", Arrays.asList("Static", "Gradient"));
    public final IntSetting red = register("Red", 20, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));
    public final IntSetting green = register("Green", 250, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));
    public final IntSetting blue = register("Blue", 20, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));

    public final FloatSetting step = register("Step", 0.3f, 0.01f, 2.0f).invokeVisibility(z -> !mode.getValue().equals("Static"));
    public final FloatSetting speed = register("Speed", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> !mode.getValue().equals("Static"));
    private final BooleanSetting randoms = register("Randomize", false);

    public final IntSetting gradientRed1 = register("Red1", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientGreen1 = register("Green1", 155, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientBlue1 = register("Blue1", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));

    public final IntSetting gradientRed2 = register("Red2", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientGreen2 = register("Green2", 255, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientBlue2 = register("Blue2", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));

    public final IntSetting gradientRed3 = register("Red3", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientGreen3 = register("Green3", 255, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientBlue3 = register("Blue3", 5, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    private int tickDelay = 0;
    private final int maxTickDelay = 20;

    // private final BooleanSetting snowing = register("Snowing", true);

    public ClickGui() {
        bind.invokeValue(Keyboard.KEY_O);
        Instance = this;
    }


    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (randoms.getValue()) {
            if (tickDelay == 0) {
                gradientRed1.setValue(random.nextInt(256));
                gradientGreen1.setValue(random.nextInt(256));
                gradientBlue1.setValue(random.nextInt(256));
                gradientRed2.setValue(random.nextInt(256));
                gradientGreen2.setValue(random.nextInt(256));
                gradientBlue2.setValue(random.nextInt(256));
                gradientRed3.setValue(random.nextInt(256));
                gradientGreen3.setValue(random.nextInt(256));
                gradientBlue3.setValue(random.nextInt(256));
            }
            tickDelay = (tickDelay + 1) % maxTickDelay;
        }
        if (!(mc.currentScreen instanceof GrassWareGui)) {
            disable();
        }
    }

    public Color getColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue());
    }

    public Color getColorAlpha() {
        return new Color(red.getValue(), green.getValue(), blue.getValue(), 200);
    }

    public Color[] getGradient() {
        return new Color[]{
                new Color(gradientRed1.getValue(), gradientGreen1.getValue(), gradientBlue1.getValue()),
                new Color(gradientRed2.getValue(), gradientGreen2.getValue(), gradientBlue2.getValue()),
                new Color(gradientRed3.getValue(), gradientGreen3.getValue(), gradientBlue3.getValue())
        };
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(GrassWareGui.getClickGui());
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
    }

    @EventListener
    public void onTick(final TickEvent event) {
        if (!(mc.currentScreen instanceof GrassWareGui)) {
            disable();
        }
    }
}

