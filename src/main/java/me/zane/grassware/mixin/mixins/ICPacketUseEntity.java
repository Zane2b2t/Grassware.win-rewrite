package me.zane.grassware.mixin.mixins;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketUseEntity.class)
public interface ICPacketUseEntity {

    @Accessor("entityId")
    void setEntityId(int entityId);

    @Accessor("action")
    void setAction(CPacketUseEntity.Action action);
}
