package me.zane.grassware.mixin.mixins;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void addEffect(final Particle particle, final CallbackInfo ci) {
        final ParticleEvent event = new ParticleEvent();
        GrassWare.eventBus.invoke(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}