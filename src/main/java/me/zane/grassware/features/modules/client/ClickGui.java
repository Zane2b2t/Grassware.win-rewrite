package me.zane.grassware.features.modules.client;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.gui.OyVeyGui;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;

public class ClickGui extends Module {
    public static ClickGui Instance;
    public final ModeSetting mode = register("Mode", "Static", Arrays.asList("Static", "Gradient"));
    public final IntSetting red = register("Red", 68, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));
    public final IntSetting green = register("Green", 0, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));
    public final IntSetting blue = register("Blue", 152, 0, 255).invokeVisibility(z -> mode.getValue().equals("Static"));

    public final FloatSetting step = register("Step", 1.0f, 0.1f, 2.0f).invokeVisibility(z -> !mode.getValue().equals("Static"));
    public final FloatSetting speed = register("Speed", 1.0f, 0.1f, 5.0f).invokeVisibility(z -> !mode.getValue().equals("Static"));

    public final IntSetting gradientRed1 = register("Red1", 68, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientGreen1 = register("Green1", 0, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientBlue1 = register("Blue1", 152, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));

    public final IntSetting gradientRed2 = register("Red2", 68, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientGreen2 = register("Green2", 255, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    public final IntSetting gradientBlue2 = register("Blue2", 152, 0, 255).invokeVisibility(z -> mode.getValue().equals("Gradient"));
    
    private final BooleanSetting snowing = register("Snowing", false);

    public ClickGui() {
        bind.invokeValue(Keyboard.KEY_O);
        Instance = this;
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
                new Color(gradientRed2.getValue(), gradientGreen2.getValue(), gradientBlue2.getValue())
        };
    }

    @EventListener
    public void onRender3D(final Render3DEvent event){
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(OyVeyGui.getClickGui());
    }

    @Override
    public void onDisable(){
        mc.displayGuiScreen(null);
    }

    @EventListener
    public void onTick(final TickEvent event) {
        if (!(mc.currentScreen instanceof OyVeyGui)) {
            disable();
        }
    }
}

