package net.changed.network.packet;

import net.changed.network.packet.debugger.FacilityAddPiecesPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.changed.network.legacy.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class DebuggerPacket implements ChangedPacket {
    public static abstract class Payload implements ChangedPacket {
        private final ResourceLocation identifier;

        public Payload(ResourceLocation identifier) {
            this.identifier = identifier;
        }

        public final ResourceLocation getIdentifier() {
            return identifier;
        }

        public DebuggerPacket wrap() {
            return DebuggerPacket.of(this);
        }
    }

    private final ResourceLocation identifier;
    private final Payload payload;

    private static final Map<ResourceLocation, Function<FriendlyByteBuf, Payload>> SUB_HANDLERS = new HashMap<>();

    public static void registerDebugPacket(ResourceLocation identifier, Function<FriendlyByteBuf, Payload> packetReader) {
        SUB_HANDLERS.put(identifier, packetReader);
    }

    public DebuggerPacket(Payload payload) {
        this.identifier = payload.getIdentifier();
        this.payload = payload;
    }

    public DebuggerPacket(FriendlyByteBuf buffer) {
        this.identifier = buffer.readResourceLocation();
        this.payload = SUB_HANDLERS.get(this.identifier).apply(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.identifier);
        this.payload.write(buffer);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        return this.payload.handle(context, levelFuture, sidedExecutor);
    }

    public static DebuggerPacket of(Payload payload) {
        return new DebuggerPacket(payload);
    }
}
