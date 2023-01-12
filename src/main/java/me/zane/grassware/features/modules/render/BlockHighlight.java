
package me.zane.grassware.features.modules.render;

import java.awt.Color;

import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BlockHighlight
extends Module {

    public final IntSetting alpha = register("Alpha", 90, 0, 255);
    public final BooleanSetting box = register("Box", true);
    public final IntSetting boxAlpha = register("BoxAlpha", 90, 0, 255);
    public final BooleanSetting outline = register("Outline", true);
    public final FloatSetting linewidth = register("LineWidth", 1, 0, 5);


    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
        }
    }
}   
