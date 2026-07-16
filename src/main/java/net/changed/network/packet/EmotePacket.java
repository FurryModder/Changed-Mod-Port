package net.changed.network.packet;

import net.changed.entity.Emote;
import net.changed.process.ProcessEmote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class EmotePacket implements ChangedPacket {
    private final int entity;
    private final Emote emote;

    public EmotePacket(int entity, Emote emote) {
        this.entity = entity;
        this.emote = emote;
    }

    public EmotePacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readVarInt();
        this.emote = Emote.values()[buffer.readInt()];
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entity);
        buffer.writeInt(emote.ordinal());
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (level.getEntity(entity) instanceof Player player)
                    ProcessEmote.playerEmote(player, emote);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
