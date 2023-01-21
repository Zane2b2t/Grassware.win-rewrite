package me.zane.grassware.features.modules.render;

import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.event.events.Render2DEvent;
import me.zane.grassware.event.events.RenderItemInFirstPersonEvent;
// import me.zane.grassware.event.events.RenderHandEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.mixin.mixins.IEntityRenderer;

import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.event.RenderHandEvent;
// import net.minecraft.world.item.ItemStack;



public class ItemChams extends Module{
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting linewidth = register("LineWidth", 1.0f, 0.0f, 5.0f);
    private final BooleanSetting yes = register("Yes", false);


    @EventListener
    public void onRender2D(final Render2DEvent event) {
       if (this.yes.getValue()) {
           frameBufferShader.startDraw(event.getPartialTicks());
// my first module made from scratch :D -ZANE 1/19/2022 (intelij so ez wtf)

            ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
           frameBufferShader.stopDraw((float)this.linewidth.getValue(), (float)this.opacity.getValue());
       }
        GradientShader.setup(
                ClickGui.Instance.step.getValue(),
                ClickGui.Instance.speed.getValue(),
                ClickGui.Instance.getGradient()[0],
                ClickGui.Instance.getGradient()[1],
                opacity.getValue()
        );
        GradientShader.finish();
    }
    @EventListener
    public void onRenderHand(final RenderItemInFirstPersonEvent event);
    
    @EventListener
     private void renderHandEvent(RenderHandEvent event) {
        
     }

} 
