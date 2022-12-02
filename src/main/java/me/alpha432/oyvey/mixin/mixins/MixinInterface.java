package me.alpha432.oyvey.mixin.mixins;

import net.minecraft.client.Minecraft;


public interface MixinInterface {
	
	
	Minecraft mc = Minecraft.getMinecraft();
	boolean nullCheck = (mc.player == null || mc.world == null);
}
