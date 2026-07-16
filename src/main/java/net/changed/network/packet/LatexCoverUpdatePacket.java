package net.changed.network.packet;

import net.changed.init.ChangedLatexTypes;
import net.changed.util.UniversalDist;
import net.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class LatexCoverUpdatePacket implements ChangedPacket {
    private final BlockPos pos;
    private final LatexCoverState latexCoverState;

    public LatexCoverUpdatePacket(BlockPos pos, LatexCoverState state) {
        this.pos = pos;
        this.latexCoverState = state;
    }

    public LatexCoverUpdatePacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.latexCoverState = ChangedLatexTypes.getLatexCoverStateIDMap().byIdOrThrow(buffer.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarInt(ChangedLatexTypes.getLatexCoverStateIDMap().getIdOrThrow(this.latexCoverState));
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                LatexCoverState.setServerVerifiedAt(level, pos, latexCoverState, 19);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
