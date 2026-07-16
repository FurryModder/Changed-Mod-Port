package net.changed.compat;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class ForgeEventFactory {
    private ForgeEventFactory() {}

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
        return state;
    }

    public static boolean fireSleepingTimeCheck(Player player, Optional<BlockPos> sleepingLocation) {
        return true;
    }
}
