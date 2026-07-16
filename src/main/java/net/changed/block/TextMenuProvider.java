package net.changed.block;

import net.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.changed.network.legacy.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public interface TextMenuProvider {
    @Nullable AbstractContainerMenu createMenu(BlockState blockState, BlockGetter level, BlockPos blockPos, int id, Inventory inv, Player player);

    default void openMenu(Player player, MenuProvider provider, BlockPos blockPos) {
        if (player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, provider, blockPos);
        }
    }

    default void openMenu(ServerPlayer player, MenuProvider provider, BlockPos blockPos) {
        NetworkHooks.openScreen(player, provider, extra -> {
            extra.writeBlockPos(blockPos);
            extra.writeUtf(player.level().getBlockEntity(blockPos, ChangedBlockEntities.TEXT_BLOCK_ENTITY.get())
                    .map(blockEntity -> blockEntity.text)
                    .orElse(""));
        });
    }
}
