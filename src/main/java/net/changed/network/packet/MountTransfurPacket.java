package net.changed.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MountTransfurPacket implements ChangedPacket {
    private final int entity;
    private final int mount;

    public MountTransfurPacket(int entity, int mount) {
        this.entity = entity;
        this.mount = mount;
    }

    public MountTransfurPacket(FriendlyByteBuf buffer) {
        this.entity = buffer.readVarInt();
        this.mount = buffer.readVarInt();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entity);
        buffer.writeVarInt(mount);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (entity == mount)
                    level.getEntity(entity).stopRiding();
                else
                    level.getEntity(entity).startRiding(level.getEntity(mount));
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
