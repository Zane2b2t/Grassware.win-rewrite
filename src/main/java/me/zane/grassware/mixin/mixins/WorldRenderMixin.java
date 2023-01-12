package us.velocity.client.impl.mixins;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.util.hit.HitResult;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.zane.grassware.features.modules.render.BlockHighlight;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin
{

    //no use but will keep incase
//    @Inject(at = @At("HEAD"), method = "method_1547", cancellable = true)
//    private void setupBlockBreakOverlay(CallbackInfo ci) {
//        ci.cancel();
//    }

    @Inject(method = "method_1554", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void setupBlockHighlight(PlayerBase arg, HitResult arg1, int i, ItemInstance arg2, float f, CallbackInfo ci) {
        if(ModuleManager.getModuleByClass(BlockHighlight.class).isEnabled()) {
            GL11.glColor4d(BlockHighlight.r.getValue(), BlockHighlight.g.getValue(), BlockHighlight.b.getValue(), BlockHighlight.a.getValue());
        }
    }

    @Inject(method = "method_1554", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void setupBlockHighlightLineWidth(PlayerBase arg, HitResult arg1, int i, ItemInstance arg2, float f, CallbackInfo ci) {
        if(ModuleManager.getModuleByClass(BlockHighlight.class).isEnabled()) {
            GL11.glLineWidth(BlockHighlight.lineWidth.getValue().floatValue());
        }
    }

    //could be used for xray?
//    @Inject(at = @At("HEAD"), method = "method_1543", cancellable = true)
//    private void setupBlockRender(CallbackInfo ci) {
//        ci.cancel();
//    }
}
