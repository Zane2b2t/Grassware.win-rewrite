package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private final Packet<?> packet;


    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }

    public static class Send extends PacketEvent {
        public Send(final Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }
}

