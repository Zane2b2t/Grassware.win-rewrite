package me.zane.grassware.features.modules.render;

import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.modules.Module;

public class Hand extends Module {

    public static Hand INSTANCE = new Hand();
    public static boolean rendering;

    public final BooleanSetting renderGlintOnce = register("RenderGlintOnce", true);
    public final IntSetting red = register("Red", 255, 0, 255);
    public final IntSetting green = register("Green", 255, 0, 255);
    public final IntSetting blue = register("Blue", 255, 0, 255);

    public final IntSetting alpha = register("Opacity", 255, 0, 255);

}