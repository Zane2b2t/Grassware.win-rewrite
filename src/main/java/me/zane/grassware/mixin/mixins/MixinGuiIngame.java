package me.zane.grassware.mixin.mixins;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.RenderHotbarEvent;
import me.zane.grassware.event.events.RenderPotionEffectsEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    private void renderPotionEffects(final ScaledResolution resolution, final CallbackInfo ci) {
        final RenderPotionEffectsEvent renderPotionEffectsEvent = new RenderPotionEffectsEvent();
        GrassWare.eventBus.invoke(renderPotionEffectsEvent);
        if (renderPotionEffectsEvent.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(final ScaledResolution sr, final float partialTicks, final CallbackInfo ci) {
        final RenderHotbarEvent renderHotbarEvent = new RenderHotbarEvent(sr);
        GrassWare.eventBus.invoke(renderHotbarEvent);
        if (renderHotbarEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
