package me.zane.grassware.features.modules.render;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

public class BlockHighlight
        extends Module {

    public final BooleanSetting box = register("Box", true);
    public final IntSetting opacity = register("Opacity", 90, 0, 255);
    public final BooleanSetting outline = register("Outline", true);
    public final FloatSetting lineWidth = register("LineWidth", 1f, 0f, 5f);


    @EventListener
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        BlockPos pos = ray.getBlockPos();
        GradientShader.setup(opacity.getValue().floatValue() / 255f);
        if (box.getValue())
            RenderUtil.boxShader(pos);
        if (outline.getValue()) {
            GL11.glLineWidth(lineWidth.getValue());
            RenderUtil.outlineShader(pos);
            RenderUtil.outlineShader(pos);
        }
        GradientShader.finish();
    }
}
