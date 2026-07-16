package net.changed.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlantable {
    default BlockState getPlant(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos);
    }
}
