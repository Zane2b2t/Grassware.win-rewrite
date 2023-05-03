package me.zane.grassware.mixin.mixins.optmization;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedQuad.class)
public class MixinBakedQuad {

    @Shadow
    @Final
    protected int tintIndex;

    private boolean hasTintIndex;

    @Inject(method = "<init>([IILnet/minecraft/util/EnumFacing;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;ZLnet/minecraft/client/renderer/vertex/VertexFormat;)V", at = @At("RETURN"))
    public void init(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting, VertexFormat format, CallbackInfo ci) {
        this.hasTintIndex = tintIndex != -1;
    }

    /**
     * @author Cache Lol
     * @return Cache Lol
     */
    @Overwrite
    public boolean hasTintIndex() {
        return hasTintIndex;
    }

}