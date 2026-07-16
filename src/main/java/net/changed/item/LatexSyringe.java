package net.changed.item;

import net.changed.Changed;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.ImmediateTransfurDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.*;
import net.changed.process.Pale;
import net.changed.process.ProcessTransfur;
import net.changed.util.TagUtil;
import net.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class LatexSyringe extends ItemNameBlockItem implements SpecializedAnimations, VariantHoldingBase {
    public LatexSyringe(Properties properties) {
        super(ChangedBlocks.DROPPED_SYRINGE.get(), properties);
    }

    @Override
    public Item getOriginalItem() {
        return ChangedItems.BLOOD_SYRINGE.get();
    }

    @Override
    public void fillItemList(Predicate<TransfurVariant<?>> predicate, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        TransfurVariant.getPublicTransfurVariants().filter(predicate).forEach(variant -> {
            output.accept(
                    Syringe.setOwner(
                            Syringe.setPureVariant(new ItemStack(this),
                                    variant.getFormId()),
                            UniversalDist.getLocalPlayer()));
        });
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, TooltipContext context, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addOwnerTooltip(null, p_43359_, p_43361_);
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack p_43001_, @Nullable LivingEntity entity) {
        return 48;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }
        ChangedSounds.broadcastSound(entity, ChangedSounds.SYRINGE_PRICK, 1, 1);
        if (player != null) {
            CompoundTag tag = TagUtil.getCustomData(stack);
            TransfurCause cause = (player.getUsedItemHand() == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.RIGHT) ? TransfurCause.SYRINGE : TransfurCause.SYRINGE_LEFT_HAND;

            if (tag != null && tag.contains("safe") && ProcessTransfur.isPlayerTransfurred(player)) {
                if (tag.getBoolean("safe"))
                    Pale.tryCure(player);
            }

            else if (tag != null && tag.contains("form")) {
                ResourceLocation formLocation = ResourceLocation.parse(tag.getString("form"));
                ProcessTransfur.transfur(entity, ImmediateTransfurDecision.unsafe(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formLocation), cause));
            }

            else {
                ProcessTransfur.transfur(entity, ImmediateTransfurDecision.unsafe(ChangedTransfurVariants.FALLBACK_VARIANT.get(), cause));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            stack = new ItemStack(ChangedItems.SYRINGE.get());
        }

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    public @NotNull Rarity getRarity(ItemStack stack) {
        var tag = TagUtil.getCustomData(stack);
        if (tag == null)
            return Rarity.COMMON;
        return tag.contains("safe") ? (tag.getBoolean("safe") ? Rarity.RARE : Rarity.UNCOMMON) : Rarity.UNCOMMON;
    }

    @Nullable
    @Override
    public SpecializedAnimations.AnimationHandler getAnimationHandler() {
        return new Syringe.SyringeAnimation(this);
    }

    @Override
    public boolean triggerItemUseEffects(LivingEntity entity, ItemStack itemStack, int particleCount) {
        return true;
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnBlock extends Event implements ICancellableEvent {
        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;
        public final TransfurVariant<?> syringeVariant;

        public UsedOnBlock(BlockPos blockPos, BlockState blockState, Level level, Player player, ItemStack syringe, TransfurVariant<?> syringeVariant) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
            this.syringeVariant = syringeVariant;
        }

    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnEntity extends Event implements ICancellableEvent {
        public final LivingEntity entity;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;
        public final TransfurVariant<?> syringeVariant;

        public UsedOnEntity(LivingEntity entity, Level level, Player player, ItemStack syringe, TransfurVariant<?> syringeVariant) {
            this.entity = entity;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
            this.syringeVariant = syringeVariant;
        }

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        if (Changed.postModEvent(
                new UsedOnBlock(context.getClickedPos(),
                        clickedState,
                        context.getLevel(),
                        context.getPlayer(),
                        context.getItemInHand(),
                        Syringe.getVariant(context.getItemInHand()))))
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return super.useOn(context);

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        return Changed.postModEvent(
                new UsedOnEntity(livingEntity,
                        player.level(),
                        player,
                        itemStack,
                        Syringe.getVariant(itemStack))) ?
                InteractionResult.sidedSuccess(player.level().isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @org.jetbrains.annotations.Nullable Player player, ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

        level.getBlockEntity(pos, ChangedBlockEntities.DROPPED_SYRINGE.get()).ifPresent(droppedSyringeBlockEntity -> {
            droppedSyringeBlockEntity.setVariant(Syringe.getVariant(stack));
        });

        return result;
    }
}
