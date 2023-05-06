package me.zane.grassware.mixin.mixins;

import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {

    private static final EnumFacing[] VALUES = EnumFacing.values();

    @Redirect(method = "renderModelSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] redirectValues() {
        return VALUES;
    }

    @Redirect(method = "renderModelFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] redirectValues2() {
        return VALUES;
    }

    @Redirect(method = "renderModelBrightnessColor(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/client/renderer/block/model/IBakedModel;FFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] gr3errwrerw() {
        return VALUES;
    }

    @Redirect(method = "fillQuadBounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;values()[Lnet/minecraft/util/EnumFacing;"))
    public EnumFacing[] redirectValues3() {
        return VALUES;
    }

}