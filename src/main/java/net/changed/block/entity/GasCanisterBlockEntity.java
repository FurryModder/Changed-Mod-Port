package net.changed.block.entity;

import net.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GasCanisterBlockEntity extends BlockEntity {
    private int usage = 0;
    private boolean infinite = false; // Primarily for mapmakers

    public GasCanisterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.GAS_CANISTER.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Usage", usage);
        tag.putBoolean("Infinite", infinite);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Usage")) usage = tag.getInt("Usage");
        if (tag.contains("Infinite")) infinite = tag.getBoolean("Infinite");
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Usage", usage);
        tag.putBoolean("Infinite", infinite);
        return tag;
    }

    public int getUsage() {
        return infinite ? 0 : usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }
}
