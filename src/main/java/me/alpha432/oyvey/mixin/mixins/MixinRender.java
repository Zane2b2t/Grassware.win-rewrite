package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.FireEvent;
import me.alpha432.oyvey.event.events.NameplateEvent;
import me.alpha432.oyvey.util.MC;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class MixinRender<T extends Entity> implements MC {

    @Inject(method = "renderEntityOnFire", at = @At("HEAD"), cancellable = true)
    private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks, CallbackInfo ci){
        final FireEvent fireEvent = new FireEvent();
        OyVey.eventBus.invoke(fireEvent);
        if (fireEvent.isCancelled()){
            ci.cancel();
        }
    }

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        final NameplateEvent nameplateEvent = new NameplateEvent(entityIn);
        OyVey.eventBus.invoke(nameplateEvent);
        if (nameplateEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
