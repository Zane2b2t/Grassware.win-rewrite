package me.zane.grassware.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At(value = "HEAD"), cancellable = true)
    private void onSendPacketPre(final Packet<?> packet, final CallbackInfo info) {
        final PacketEvent.Send event = new PacketEvent.Send(packet);
        GrassWare.eventBus.invoke(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "channelRead0*", at = @At(value = "HEAD"), cancellable = true)
    private void onChannelReadPre(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo info) {
        final PacketEvent.Receive event = new PacketEvent.Receive(packet);
        GrassWare.eventBus.invoke(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }
}

