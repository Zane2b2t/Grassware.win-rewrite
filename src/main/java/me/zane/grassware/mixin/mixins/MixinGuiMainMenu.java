package me.zane.grassware.mixin.mixins;

import me.zane.grassware.features.gui.alt.AltGui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        AltGui.drawScreen(mouseX, mouseY);
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void keyTyped(final char typedChar, final int keyCode, final CallbackInfo ci) {
        AltGui.keyTyped(typedChar, keyCode);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(final int mouseX, final int mouseY, final int mouseButton, final CallbackInfo ci) {
        AltGui.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
