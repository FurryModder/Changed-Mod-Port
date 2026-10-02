package net.furrymodder.changed.block;

import net.furrymodder.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface LatexCoveringSource {
    @NotNull
    LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos);
}
