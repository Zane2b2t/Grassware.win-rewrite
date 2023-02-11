package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.event.events.Render2DEvent;
import me.zane.grassware.event.events.RenderItemInFirstPersonEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.mixin.mixins.IEntityRenderer;

import net.minecraftforge.client.event.RenderHandEvent;

public class ItemChams extends Module {
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    
    private boolean criticalSection = true;
   
    @EventListener
    public void onRenderItemInFirstPerson(final RenderItemInFirstPersonEvent event) {
        if(criticalSection) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onRender2D(final Render2DEvent event) {
        GradientShader.setup(opacity.getValue());
        
        criticalSection = false;
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
        criticalSection = true;
        
        GradientShader.finish();
    }
    
} 
