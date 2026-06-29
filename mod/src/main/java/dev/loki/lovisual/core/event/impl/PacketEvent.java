package dev.loki.lovisual.core.event.impl;

import dev.loki.lovisual.core.event.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {
    private final Packet<?> packet;
    private final PacketState state;

    public PacketEvent(Packet<?> packet, PacketState state) {
        this.packet = packet;
        this.state = state;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public PacketState getState() {
        return state;
    }

    public enum PacketState {
        SEND, RECEIVE
    }
}
