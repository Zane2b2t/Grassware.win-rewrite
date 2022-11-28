package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ChunkLoadEvent;
import me.alpha432.oyvey.util.MC;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher implements MC {

    @Inject(method = "getNextChunkUpdate", at = @At("HEAD"))
    private void limitChunkUpdates(final CallbackInfoReturnable<ChunkCompileTaskGenerator> callbackInfoReturnable) throws InterruptedException {
        final ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(0L);
        OyVey.eventBus.invoke(chunkLoadEvent);
        if (!mc.isSingleplayer()) {
            Thread.sleep(chunkLoadEvent.delay);
        }
    }
}