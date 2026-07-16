package net.changed.network.packet;

import net.changed.Changed;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;
import net.changed.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class QueryTransfurPacket implements ChangedPacket {
    private final List<Integer> changedForms;
    private static final ResourceLocation NO_FORM = Changed.modResource("no_form");

    public QueryTransfurPacket(List<Integer> changedForms) {
        this.changedForms = changedForms;
    }

    public QueryTransfurPacket(FriendlyByteBuf buffer) {
        this.changedForms = buffer.readList(FriendlyByteBuf::readVarInt);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(changedForms, FriendlyByteBuf::writeVarInt);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ServerPlayer sender = context.getSender();
                if (sender != null) {
                    SyncTransfurPacket.Builder builder = new SyncTransfurPacket.Builder();
                    changedForms.forEach(id -> {
                        var entity = sender.level().getEntity(id);
                        if (entity instanceof Player player)
                            builder.addPlayer(player, false);
                    });
                    if (builder.worthSending()) Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), builder.build());
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));
    }

    public static class Builder {
        private final List<Integer> changedForms = new ArrayList<>();

        public void addPlayer(Player player) {
            changedForms.add(player.getId());
        }

        public QueryTransfurPacket build() {
            return new QueryTransfurPacket(changedForms);
        }

        public static QueryTransfurPacket of(Player player) {
            Builder builder = new Builder();
            builder.addPlayer(player);
            return builder.build();
        }
    }
}
