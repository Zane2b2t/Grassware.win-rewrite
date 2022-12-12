package me.zane.grassware.mixin.mixins;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.shader.impl.BlackShader;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat implements MC {

    @Shadow
    @Final
    public Minecraft mc;

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer instance, String text, float x, float y, int color) {
        if (text.contains("<grassware>")) {
            if (!ClickGui.Instance.mode.getValue().equals("Static")) {
                BlackShader.setup();
                GrassWare.textManager.renderStringShadowOnly("<grassware>", x, y);
                BlackShader.finish();
                GradientShader.setup();
                GrassWare.textManager.renderStringNoShadow("<grassware>", x, y, ClickGui.Instance.getColor());
                GradientShader.finish();
            } else {
                GrassWare.textManager.renderString(text, x, y, new Color(color));
            }
        }
        return mc.fontRenderer.drawStringWithShadow(text.replace("<grassware>", ""), x + (text.contains("<grassware>") ? GrassWare.textManager.stringWidth("<grassware>") : 0), y, color);
    }
}
