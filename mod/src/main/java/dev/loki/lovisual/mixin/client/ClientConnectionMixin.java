package dev.loki.lovisual.mixin.client;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (EventBus.post(new PacketEvent(packet, PacketEvent.PacketState.SEND)).isCancelled()) ci.cancel();
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (EventBus.post(new PacketEvent(packet, PacketEvent.PacketState.RECEIVE)).isCancelled()) ci.cancel();
    }
}
