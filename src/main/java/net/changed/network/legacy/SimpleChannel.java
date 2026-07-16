package net.changed.network.legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.changed.Changed;
import net.changed.network.PacketDistributor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SimpleChannel {
    private final ResourceLocation name;
    private final Supplier<String> protocolVersion;
    private final CustomPacketPayload.Type<LegacyPayload> type;
    private final Map<Class<?>, MessageSpec<?>> messages = new HashMap<>();
    private final Map<Integer, MessageSpec<?>> messagesById = new HashMap<>();

    private final StreamCodec<RegistryFriendlyByteBuf, LegacyPayload> codec = StreamCodec.ofMember(
            LegacyPayload::write,
            this::readPayload);

    public SimpleChannel(ResourceLocation name, Supplier<String> protocolVersion) {
        this.name = name;
        this.protocolVersion = protocolVersion;
        this.type = new CustomPacketPayload.Type<>(name);
    }

    public void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(protocolVersion.get()).playBidirectional(type, codec, this::handlePayload);
    }

    public <T> void registerMessage(int id, Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        MessageSpec<T> spec = new MessageSpec<>(id, messageType, encoder, decoder, handler);
        messages.put(messageType, spec);
        messagesById.put(id, spec);
    }

    public void sendToServer(Object message) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(payload(message));
    }

    public void send(PacketDistributor.PacketTarget target, Object message) {
        LegacyPayload payload = payload(message);
        switch (target.kind()) {
            case SERVER -> net.neoforged.neoforge.network.PacketDistributor.sendToServer(payload);
            case ALL -> net.neoforged.neoforge.network.PacketDistributor.sendToAllPlayers(payload);
            case PLAYER -> {
                Object value = target.target().get();
                if (value instanceof ServerPlayer serverPlayer) {
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, payload);
                }
            }
            case TRACKING_ENTITY -> sendToTracking(target.target().get(), payload, false);
            case TRACKING_ENTITY_AND_SELF -> sendToTracking(target.target().get(), payload, true);
        }
    }

    public Packet<? super ClientGamePacketListener> toVanillaPacket(Object message, NetworkDirection direction) {
        if (direction != NetworkDirection.PLAY_TO_CLIENT) {
            throw new IllegalArgumentException("Only clientbound legacy payloads can be wrapped as vanilla packets");
        }
        return new ClientboundCustomPayloadPacket(payload(message));
    }

    private void sendToTracking(Object value, LegacyPayload payload, boolean includeSelf) {
        if (value instanceof Entity entity) {
            if (includeSelf) {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
            } else {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
            }
        } else if (value instanceof BlockEntity blockEntity && blockEntity.getLevel() instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(blockEntity.getBlockPos());
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, payload);
        } else if (includeSelf && value instanceof ServerPlayer serverPlayer) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, payload);
        }
    }

    private LegacyPayload payload(Object message) {
        MessageSpec<?> spec = messages.get(message.getClass());
        if (spec == null) {
            throw new IllegalArgumentException("Unregistered legacy packet type " + message.getClass().getName());
        }
        return new LegacyPayload(spec.id(), message);
    }

    private LegacyPayload readPayload(RegistryFriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        MessageSpec<?> spec = messagesById.get(id);
        if (spec == null) {
            throw new IllegalArgumentException("Unknown legacy packet id " + id + " on " + name);
        }
        return new LegacyPayload(id, spec.decoder().apply(buffer));
    }

    private void writePayload(RegistryFriendlyByteBuf buffer, LegacyPayload payload) {
        MessageSpec<?> spec = messagesById.get(payload.id());
        if (spec == null) {
            throw new IllegalArgumentException("Unknown legacy packet id " + payload.id() + " on " + name);
        }

        buffer.writeVarInt(payload.id());
        writeMessage(spec, payload.message(), buffer);
    }

    @SuppressWarnings("unchecked")
    private <T> void writeMessage(MessageSpec<T> spec, Object message, FriendlyByteBuf buffer) {
        spec.encoder().accept((T)message, buffer);
    }

    private void handlePayload(LegacyPayload payload, IPayloadContext context) {
        MessageSpec<?> spec = messagesById.get(payload.id());
        if (spec == null) {
            Changed.LOGGER.error("Dropped unknown legacy packet id {} on {}", payload.id(), name);
            return;
        }

        NetworkEvent.Context legacyContext = new NetworkEvent.Context(context);
        handleMessage(spec, payload.message(), legacyContext);
    }

    @SuppressWarnings("unchecked")
    private <T> void handleMessage(MessageSpec<T> spec, Object message, NetworkEvent.Context context) {
        spec.handler().accept((T)message, () -> context);
    }

    private record LegacyPayload(int id, Object message) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return Changed.PACKET_HANDLER.type;
        }

        private void write(RegistryFriendlyByteBuf buffer) {
            Changed.PACKET_HANDLER.writePayload(buffer, this);
        }
    }

    private record MessageSpec<T>(int id, Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder,
                                  Function<FriendlyByteBuf, T> decoder,
                                  BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {}
}
