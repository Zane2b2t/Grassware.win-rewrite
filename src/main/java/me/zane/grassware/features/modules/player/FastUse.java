package me.zane.grassware.features.modules.player;
//TODO: Make DotGodRender only work on Obsidian and Bedrock
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.render.BlockHighlight;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class FastUse extends Module { //rename to fastuse bcs we add more stuff
    private final IntSetting speed = register("Delay", 0, 0, 3);
    private final BooleanSetting exp = register("EXP", false);
    private final BooleanSetting crystal = register("Crystal", false);
    private final BooleanSetting dotGodRender = register("DotGodRender", true);
    public final IntSetting opacity = register("Opacity", 90, 0, 255).invokeVisibility(z -> dotGodRender.getValue());

    public boolean condition = false;

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.world == null || mc.player == null)
            return;

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() == Items.EXPERIENCE_BOTTLE && exp.getValue()) {
            mc.rightClickDelayTimer = speed.getValue();
        }

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() == Items.END_CRYSTAL && crystal.getValue()) {
            mc.rightClickDelayTimer = speed.getValue();
            condition = true;
        }
    }
    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (crystal.getValue() && condition == true && dotGodRender.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
            if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
                return;
            BlockPos pos = ray.getBlockPos();
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(pos);
        }
    }
}