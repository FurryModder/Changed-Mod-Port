package net.changed.network.packet;

import net.changed.init.ChangedBlockEntities;
import net.changed.network.legacy.NetworkEvent;
import net.changed.world.inventory.StasisChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StasisChamberControlPacket implements ChangedPacket {
    private final BlockPos blockPos;
    private final CompoundTag payload;

    public StasisChamberControlPacket(BlockPos blockPos, CompoundTag payload) {
        this.blockPos = blockPos;
        this.payload = payload.copy();
    }

    public StasisChamberControlPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        CompoundTag tag = buffer.readNbt();
        this.payload = tag != null ? tag : new CompoundTag();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeNbt(payload);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() != LogicalSide.SERVER)
            return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));

        context.setPacketHandled(true);
        ServerPlayer player = context.getSender();
        if (player == null)
            return CompletableFuture.completedFuture(null);

        return levelFuture.thenAccept(level -> {
            if (level == null || player.level() != level)
                return;
            if (player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) > 64.0D)
                return;

            level.getBlockEntity(blockPos, ChangedBlockEntities.STASIS_CHAMBER.get())
                    .ifPresent(blockEntity -> StasisChamberMenu.handleControl(blockEntity, payload, player));
        });
    }
}
