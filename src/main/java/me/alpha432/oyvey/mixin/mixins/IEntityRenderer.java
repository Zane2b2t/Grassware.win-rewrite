package me.alpha432.oyvey.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface IEntityRenderer {
    @Invoker("setupCameraTransform")
    void invokeSetupCameraTransform(float partialTicks, int pass);

    @Invoker("renderHand")
    void invokeRenderHand(float partialTicks, int pass);
}
