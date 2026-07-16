package net.changed.network.legacy;

import java.util.concurrent.CompletableFuture;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class NetworkEvent {
    private NetworkEvent() {}

    public static class Context {
        private final IPayloadContext payloadContext;
        private boolean packetHandled;

        public Context(IPayloadContext payloadContext) {
            this.payloadContext = payloadContext;
        }

        public NetworkDirection getDirection() {
            return payloadContext.flow() == PacketFlow.CLIENTBOUND ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER;
        }

        public ServerPlayer getSender() {
            try {
                return payloadContext.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
            } catch (UnsupportedOperationException ignored) {
                return null;
            }
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            return payloadContext.enqueueWork(runnable);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean isPacketHandled() {
            return packetHandled;
        }
    }
}
