package net.changed.block.entity;

import net.changed.block.TextEnterable;
import net.changed.block.TextMenuProvider;
import net.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextBlockEntity extends BlockEntity implements MenuProvider, TextEnterable {
    public String text = "";

    public TextBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.TEXT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Text", text);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Text"))
            text = tag.getString("Text");
    }

    private static final Component NOTE = Component.translatable("container.changed.note");
    @Override
    public Component getDisplayName() {
        return NOTE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        if (this.getBlockState().getBlock() instanceof TextMenuProvider provider)
            return provider.createMenu(this.getBlockState(), player.level(), this.worldPosition, id, inv, player);
        return null;
    }

    @Override
    public void setText(String text) {
        if (this.text.isEmpty()) {
            this.text = text;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public BlockEntity getSelf() {
        return this;
    }
}
