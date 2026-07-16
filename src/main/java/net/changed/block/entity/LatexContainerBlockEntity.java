package net.changed.block.entity;

import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.latex.LatexType;
import net.changed.fluid.AbstractLatexFluid;
import net.changed.init.ChangedBlockEntities;
import net.changed.init.ChangedLatexTypes;
import net.changed.init.ChangedRegistry;
import net.changed.item.AbstractLatexBucket;
import net.changed.item.AbstractLatexItem;
import net.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;

public class LatexContainerBlockEntity extends BlockEntity {
    private LatexType currentType = ChangedLatexTypes.NONE.get();
    private byte fillLevel = 0;

    public LatexContainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.LATEX_CONTAINER.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        TagUtil.putResourceLocation(tag, "LatexType", ChangedRegistry.LATEX_TYPE.getKey(currentType));
        tag.putByte("FillLevel", fillLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("LatexType")) currentType = ChangedRegistry.LATEX_TYPE.getValue(TagUtil.getResourceLocation(tag, "LatexType", old -> {
            if ("NEUTRAL".equals(old))
                return ChangedLatexTypes.NONE.getId().toString();
            if ("DARK_LATEX".equals(old))
                return ChangedLatexTypes.DARK_LATEX.getId().toString();
            if ("WHITE_LATEX".equals(old))
                return ChangedLatexTypes.WHITE_LATEX.getId().toString();
            return old;
        }));
        if (tag.contains("FillLevel")) fillLevel = tag.getByte("FillLevel");
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        TagUtil.putResourceLocation(tag, "LatexType", ChangedRegistry.LATEX_TYPE.getKey(currentType));
        tag.putByte("FillLevel", fillLevel);
        return tag;
    }

    @Nullable
    public ItemStack tryUse(ItemStack itemStack) {
        // Remove goo
        if (itemStack.isEmpty()) {
            if (currentType.isAir() || fillLevel == 0)
                return null;
            else {
                fillLevel--;
                this.markUpdated();

                return new ItemStack(Objects.requireNonNull(currentType.getGooItem()));
            }
        } else if (itemStack.is(Items.BUCKET)) {
            if (currentType.isAir() || fillLevel < 4)
                return null;
            else {
                fillLevel -= 4;
                this.markUpdated();

                itemStack.shrink(1);
                return new ItemStack(Objects.requireNonNull(currentType.getBucketItem()));
            }
        }

        // Insert goo
        if (itemStack.getItem() instanceof AbstractLatexItem goo) {
            var type = goo.getLatexType();
            if (type.isAir() || fillLevel >= 16)
                return null;
            if (currentType.isAir() || currentType == type || fillLevel == 0) {
                currentType = type;
                fillLevel++;
                this.markUpdated();

                itemStack.shrink(1);
                return ItemStack.EMPTY;
            }
        } else if (itemStack.getItem() instanceof AbstractLatexBucket latexBucket && latexBucket.fluid.get() instanceof AbstractLatexFluid latexFluid) {
            var type = latexFluid.getLatexType();
            if (type == null || type.isAir() || fillLevel > 12)
                return null;
            if (currentType.isAir() || currentType == type || fillLevel == 0) {
                currentType = type;
                fillLevel += 4;
                this.markUpdated();

                itemStack.shrink(1);
                return new ItemStack(Items.BUCKET);
            }
        }

        return null;
    }

    public int getFillLevel() {
        return fillLevel;
    }

    public LatexType getFillType() {
        return currentType;
    }

    public @Nullable LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        final var variant = this.getFillType().getTransfurVariant(TransfurCause.LATEX_CONTAINER_FELL, target.getRandom());

        if (variant == null)
            return null;

        return LatexAssimilationDecision.fromBlockOrItem(variant, TransfurContext.hazard(TransfurCause.LATEX_CONTAINER_FELL), 15.0f);
    }
}
