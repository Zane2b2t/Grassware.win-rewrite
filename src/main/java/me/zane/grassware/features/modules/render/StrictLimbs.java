package me.zane.grassware.features.modules.render;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StrictLimbs extends Module {

    public BooleanSetting self = new BooleanSetting("Self", true);

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if(nullCheck()) return;
        for(Entity entity : mc.world.loadedEntityList) {
            if(!(entity instanceof EntityPlayer)) return;
            if(entity.equals(mc.player) && !self.getValue()) return;
            ((EntityPlayer) entity).prevLimbSwingAmount = 0;
            ((EntityPlayer) entity).limbSwingAmount = 0;
            ((EntityPlayer) entity).limbSwing = 0;
        }
    }
}