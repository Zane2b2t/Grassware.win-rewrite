package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.ArmorEvent;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayerBipedArmor.class)
public class MixinRenderArmor {

    @Redirect(method = "setModelSlotVisible(Lnet/minecraft/client/model/ModelBiped;Lnet/minecraft/inventory/EntityEquipmentSlot;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelRenderer;showModel:Z"))
    private void showModel(ModelRenderer instance, boolean value) {
        final ArmorEvent armorEvent = new ArmorEvent();
        OyVey.eventBus.invoke(armorEvent);
        instance.showModel = !armorEvent.isCancelled();
    }
}

