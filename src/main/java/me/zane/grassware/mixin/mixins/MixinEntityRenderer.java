package me.zane.grassware.mixin.mixins;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.HurtCamEvent;
import me.zane.grassware.util.MC;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer implements MC {

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurtCamEffect(final float partialTicks, final CallbackInfo ci) {
        final HurtCamEvent hurtCamEvent = new HurtCamEvent();
        GrassWare.eventBus.invoke(hurtCamEvent);
        if (hurtCamEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
