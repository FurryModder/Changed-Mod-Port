package net.changed.network.packet;

import net.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SyncTransfurProgressPacket implements ChangedPacket {
    private final int id;
    private final float progress;

    public SyncTransfurProgressPacket(int id, float progress) {
        this.id = id;
        this.progress = progress;
    }

    public SyncTransfurProgressPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.progress = buffer.readFloat();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeFloat(progress);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var entity = level.getEntity(id);
                if (!(entity instanceof Player player))
                    return;
                var oldProgress = ProcessTransfur.getPlayerTransfurProgress(player);
                if (Math.abs(oldProgress - progress) < 0.02f) // Prevent sync shudder
                    return;
                ProcessTransfur.setPlayerTransfurProgress(player, progress);
                context.setPacketHandled(true);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
