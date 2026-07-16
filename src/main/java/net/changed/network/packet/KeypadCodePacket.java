package net.changed.network.packet;

import net.changed.block.entity.KeypadBlockEntity;
import net.changed.init.ChangedBlockEntities;
import net.changed.network.legacy.NetworkEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class KeypadCodePacket implements ChangedPacket {
    private final BlockPos blockPos;
    private final byte[] code;

    public KeypadCodePacket(BlockPos blockPos, List<Byte> code) {
        this.blockPos = blockPos;
        this.code = new byte[code.size()];
        for (int i = 0; i < this.code.length; ++i)
            this.code[i] = code.get(i);
    }

    public KeypadCodePacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.code = buffer.readByteArray(8);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeByteArray(code);
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

            KeypadBlockEntity blockEntity = level.getBlockEntity(blockPos, ChangedBlockEntities.KEYPAD.get()).orElse(null);
            if (blockEntity == null)
                return;

            List<Byte> codeList = new ArrayList<>(code.length);
            for (byte value : code)
                codeList.add(value);
            blockEntity.useCode(codeList);
        });
    }
}
