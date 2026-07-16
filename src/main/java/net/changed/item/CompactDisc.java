package net.changed.item;

import net.changed.block.Computer;
import net.changed.computers.DiscData;
import net.changed.init.ChangedBlocks;
import net.changed.init.ChangedTabs;
import net.changed.util.TagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CompactDisc extends ItemNameBlockItem {
    public static final String TAG_TITLE = "title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_DATA = "data";
    public static final String TAG_TRANSLATE = "translate";

    public CompactDisc() {
        super(ChangedBlocks.CD_STACK.get(), new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> hoverText, TooltipFlag tooltipFlag) {
        CompoundTag tag = TagUtil.getCustomData(stack);
        if (tag != null) {
            String s = DiscData.getName(tag);
            if (!StringUtil.isNullOrEmpty(s)) {
                hoverText.add((Component.translatable("text.changed.compact_disc.title", s)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public InteractionResult useOn(UseOnContext p_43466_) {
        Level level = p_43466_.getLevel();
        BlockPos blockpos = p_43466_.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(ChangedBlocks.COMPUTER.get())) {
            return Computer.tryUseDisk(p_43466_.getPlayer(), level, blockpos, blockstate, p_43466_.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        } else {
            return super.useOn(p_43466_);
        }
    }

    public boolean isFoil(ItemStack stack) {
        return TagUtil.getCustomData(stack) != null;
    }
}
