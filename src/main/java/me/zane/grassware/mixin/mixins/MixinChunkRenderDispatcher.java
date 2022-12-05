package me.zane.grassware.mixin.mixins;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.ChunkLoadEvent;
import me.zane.grassware.util.MC;
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
        GrassWare.eventBus.invoke(chunkLoadEvent);
        if (!mc.isSingleplayer()) {
            Thread.sleep(chunkLoadEvent.delay);
        }
    }
}