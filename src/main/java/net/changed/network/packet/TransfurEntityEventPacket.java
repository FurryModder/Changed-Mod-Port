package net.changed.network.packet;

import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class TransfurEntityEventPacket implements ChangedPacket {
    private final int entityId;
    private final byte eventId;

    public TransfurEntityEventPacket(Player host, byte event) {
        entityId = host.getId();
        eventId = event;
    }

    public TransfurEntityEventPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.eventId = buffer.readByte();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeByte(this.eventId);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(level.getEntity(entityId)), variant -> {
                    variant.getChangedEntity().handleEntityEvent(eventId);
                    context.setPacketHandled(true);
                });
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
