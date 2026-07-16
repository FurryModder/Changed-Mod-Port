package net.changed.block.entity;

import net.changed.block.KeypadBlock;
import net.changed.init.ChangedBlockEntities;
import net.changed.init.ChangedBlocks;
import net.changed.init.ChangedSounds;
import net.changed.world.inventory.KeypadMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeypadBlockEntity extends BlockEntity implements MenuProvider {
    public byte[] code = null;

    public KeypadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.KEYPAD.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (code != null)
            tag.putByteArray("Code", code);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Code"))
            code = tag.getByteArray("Code");
    }

    private static final Component SET_PASSWORD = Component.translatable("container.changed.keypad_first");
    private static final Component ENTER_PASSWORD = Component.translatable("container.changed.keypad");
    @Override
    public Component getDisplayName() {
        if (this.code == null)
            return SET_PASSWORD;
        else
            return ENTER_PASSWORD;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new KeypadMenu(id, inv, this.worldPosition, this.getBlockState(), this);
    }

    private void playSound(DeferredHolder<SoundEvent, SoundEvent> event, float volume, float pitch) {
        if (level instanceof ServerLevel serverLevel)
            ChangedSounds.broadcastSound(serverLevel, event, this.worldPosition, volume, pitch);
    }

    private void playUnlockSuccess() {
        playSound(ChangedSounds.KEYPAD_UNLOCK_SUCCESS, 1, 1);
    }

    private void playUnlockFail() {
        playSound(ChangedSounds.KEYPAD_UNLOCK_FAIL, 1, 1);
    }

    private void playLock() {
        playSound(ChangedSounds.KEYPAD_LOCK, 1, 1);
    }

    private void markUpdated() {
        this.setChanged();
        if (level != null)
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    private void setCode(List<Byte> newCode) {
        if (code != null)
            return;
        code = new byte[newCode.size()];
        for (int i = 0; i < code.length; ++i)
            code[i] = newCode.get(i);
        markUpdated();
    }

    public void useCode(List<Byte> attemptedCode) {
        if (level == null)
            return;

        if (this.code == null) { // Set code and lock
            setCode(attemptedCode);
            var nState = this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.FALSE);
            level.setBlockAndUpdate(this.worldPosition, nState);
            this.setBlockState(nState);
            markUpdated();
            this.playLock();
            return;
        }

        if (!this.getBlockState().getValue(KeypadBlock.POWERED)) {
            if (attemptedCode.size() != code.length) {
                this.playUnlockFail();
                return;
            }

            for (int idx = 0; idx < code.length; ++idx) {
                if (attemptedCode.get(idx) != code[idx]) {
                    this.playUnlockFail();
                    return;
                }
            }

            var nState = this.getBlockState().setValue(KeypadBlock.POWERED, Boolean.TRUE);
            level.setBlockAndUpdate(this.worldPosition, nState);
            this.setBlockState(nState);
            ChangedBlocks.KEYPAD.get().updateNeighbours(nState, level, worldPosition);
            level.scheduleTick(worldPosition, nState.getBlock(), 20);
            markUpdated();
            this.playUnlockSuccess();
        }
    }
}
