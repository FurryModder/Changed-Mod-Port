package net.changed.network.packet;

import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.entity.BasicPlayerInfo;
import net.changed.entity.PlayerDataExtension;
import net.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;
import net.changed.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class BasicPlayerInfoPacket implements ChangedPacket {
    public static final BasicPlayerInfoPacket EMPTY = new BasicPlayerInfoPacket(Map.of());

    private final Map<Integer, BasicPlayerInfo> playerInfos;
    public BasicPlayerInfoPacket(Map<Integer, BasicPlayerInfo> playerInfos) {
        this.playerInfos = playerInfos;
    }

    public BasicPlayerInfoPacket(FriendlyByteBuf buffer) {
        this.playerInfos = new HashMap<>();
        buffer.readList(next ->
                new Pair<>(next.readVarInt(), new BasicPlayerInfo(next.readNbt()))).forEach(pair ->
                playerInfos.put(pair.getFirst(), pair.getSecond()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(playerInfos.entrySet(), (next, form) -> {
            next.writeVarInt(form.getKey());
            CompoundTag bpiTag = new CompoundTag();
            form.getValue().save(bpiTag);
            next.writeNbt(bpiTag);
        });
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                Player localPlayer = UniversalDist.getLocalPlayer();

                if (!playerInfos.isEmpty()) {
                    Objects.requireNonNull(level);
                    playerInfos.forEach((id, listing) -> {
                        var entity = level.getEntity(id);
                        if (entity instanceof PlayerDataExtension ext && entity != localPlayer) {
                            ext.getBasicPlayerInfo().copyFrom(listing);
                        }
                    });
                }

                else {
                    Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(localPlayer));
                }
            });
        }

        else { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                if (sender instanceof PlayerDataExtension ext && playerInfos.containsKey(sender.getId())) {
                    BasicPlayerInfo received = playerInfos.get(sender.getId());
                    ext.getBasicPlayerInfo().copyFrom(received); // Keep player info state

                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender),
                            new BasicPlayerInfoPacket(Map.of(sender.getId(), received)));
                }
            }
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }
    }

    public static class Builder {
        private final Map<Integer, BasicPlayerInfo> playerInfos = new HashMap<>();

        public void addPlayer(Player player) {
            if (player instanceof PlayerDataExtension ext) {
                playerInfos.put(player.getId(), ext.getBasicPlayerInfo());
            }
        }

        public boolean worthSending() {
            return !playerInfos.isEmpty();
        }

        public BasicPlayerInfoPacket build() {
            return new BasicPlayerInfoPacket(playerInfos);
        }

        public static BasicPlayerInfoPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
