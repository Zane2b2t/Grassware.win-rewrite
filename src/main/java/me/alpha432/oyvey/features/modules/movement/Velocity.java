package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.PushBlockEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module {

    @EventListener
    public void onPacketReceive(final PacketEvent.Receive event){
        if ((event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId) || event.getPacket() instanceof SPacketExplosion){
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onPushBlock(final PushBlockEvent event){
        event.setCancelled(true);
    }

    @Override
    public String getInfo() {
        return " %0, %0";
    }
}