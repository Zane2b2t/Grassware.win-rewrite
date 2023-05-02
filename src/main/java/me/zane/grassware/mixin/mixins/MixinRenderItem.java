package me.zane.grassware.mixin.mixins;

import net.minecraft.client.renderer.RenderItem;
import me.zane.grassware.event.events.RenderItemEvent;
import me.zane.grassware.event.events.RenderItemInFirstPersonEvent;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

<<<<<<< Updated upstream
//@Mixin(value={RenderItem.class})
//public class MixinRenderItem {
//@Shadow
//private void renderModel(IBakedModel model, int color, ItemStack stack) {
//}

//@ModifyArg(method={"renderEffect"}, at=@At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index=1)
//private int renderEffect(int oldValue) {
//return;
//  }
//}
=======
@Mixin(value={RenderItem.class})
public class MixinRenderItem {
    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
    }

    @ModifyArg(method = {"renderEffect"}, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    private int renderEffect(int oldValue) {

        return oldValue;
    }
}
>>>>>>> Stashed changes
