package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.shader.impl.BlackShader;
import me.alpha432.oyvey.shader.impl.GradientShader;
import me.alpha432.oyvey.util.MC;
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

    @Shadow @Final public Minecraft mc;

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer instance, String text, float x, float y, int color) {
        if (text.contains("<OyVey>")) {
            if (!ClickGui.Instance.mode.getValue().equals("Static")) {
                BlackShader.setup();
                OyVey.textManager.renderStringShadowOnly("<OyVey>", x, y);
                BlackShader.finish();
                GradientShader.setup();
                OyVey.textManager.renderStringNoShadow("<OyVey>", x, y, ClickGui.Instance.getColor());
                GradientShader.finish();
            } else {
                OyVey.textManager.renderString(text, x , y, new Color(color));
            }
        }
        return mc.fontRenderer.drawStringWithShadow(text.replace("<OyVey>", ""), x + (text.contains("<OyVey>") ? OyVey.textManager.stringWidth("<OyVey>") : 0), y, color);
    }
}
