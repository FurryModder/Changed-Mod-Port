package net.changed.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.changed.Changed;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.*;
import net.changed.process.Pale;
import net.changed.process.ProcessTransfur;
import net.changed.util.TagUtil;
import net.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Syringe extends Item implements SpecializedAnimations {
    public Syringe(Properties p_41383_) {
        super(p_41383_);
    }

    public static ItemStack setPureVariant(ItemStack itemStack, ResourceLocation variant) {
        TagUtil.updateCustomData(itemStack, tag -> {
            TagUtil.putResourceLocation(tag, "form", variant);
            tag.putBoolean("safe", true);
        });
        return itemStack;
    }

    public static ItemStack setUnpureVariant(ItemStack itemStack, ResourceLocation variant) {
        TagUtil.updateCustomData(itemStack, tag -> {
            TagUtil.putResourceLocation(tag, "form", variant);
            tag.putBoolean("safe", false);
        });
        return itemStack;
    }

    public static ItemStack setVariant(ItemStack itemStack, ResourceLocation variant) {
        TagUtil.updateCustomData(itemStack, tag -> TagUtil.putResourceLocation(tag, "form", variant));
        return itemStack;
    }

    public static ItemStack setOwner(ItemStack itemStack, @Nullable Player player) {
        if (player == null)
            return itemStack;
        TagUtil.updateCustomData(itemStack, tag -> {
            tag.putUUID("owner", player.getUUID());
            tag.putString("owner_name", player.getGameProfile().getName());
        });
        return itemStack;
    }

    public static void addOwnerTooltip(@Nullable Level level, ItemStack stack, List<Component> builder) {
        var tag = TagUtil.getCustomData(stack);
        if (tag != null && tag.contains("owner")) {
            Player player = level != null ? level.getPlayerByUUID(tag.getUUID("owner")) : null;
            if (player != null)
                builder.add(Component.translatable("text.changed.syringe.owner", player.getName()));
            else if (tag.contains("owner_name"))
                builder.add(Component.translatable("text.changed.syringe.owner", Component.literal(tag.getString("owner_name"))));
            else
                builder.add(Component.translatable("text.changed.syringe.no_owner"));
        }
    }

    public static void addVariantTooltip(ItemStack stack, List<Component> builder) {
        var tag = TagUtil.getCustomData(stack);
        if (tag != null && tag.contains("form")) {
            builder.add(Component.translatable(getVariantDescriptionId(stack)));
        }
    }

    public static String getVariantDescriptionId(ItemStack stack) {
        var tag = TagUtil.getCustomData(stack);
        if (tag == null)
            return ChangedTransfurVariants.FALLBACK_VARIANT.get().getEntityType().getDescriptionId();
        TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(tag, "form"));
        if (variant == null)
            return "entity." + TagUtil.getResourceLocation(tag, "form").toString().replace("form_", "")
                    .replace(':', '.').replace('/', '_');
        return variant.getEntityType().getDescriptionId();
    }

    public static TransfurVariant<?> getVariant(ItemStack p_43364_) {
        var tag = TagUtil.getCustomData(p_43364_);
        if (tag != null) {
            if (tag.contains("form")) {
                return ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(tag, "form"));
            }
        }

        return ChangedTransfurVariants.FALLBACK_VARIANT.get();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public @NotNull ItemStack usedOnPlayer(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player, @NotNull Player sourcePlayer, boolean ignoreMovement) {
        if (!ignoreMovement && player.getDeltaMovement().lengthSqr() > 0.01f)
            return stack;
        if (ProcessTransfur.isPlayerLatex(player) && player != sourcePlayer)
            return stack;

        Vec3 movementBeforePrick = player.getDeltaMovement();
        if (player.hurt(ChangedDamageSources.BLOODLOSS.source(player.level().registryAccess()), 1.0f))
            player.setDeltaMovement(movementBeforePrick);
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", player.getUUID());
        tag.putString("owner_name", player.getGameProfile().getName());

        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            ResourceLocation form = variant.getFormId();
            ItemStack nStack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
            tag.putBoolean("safe", Pale.isCured(player));
            tag.putString("form", form.toString());
            TagUtil.setCustomData(nStack, tag);

            if (!player.addItem(nStack))
                player.drop(nStack, false);
        }, () -> {
            ItemStack nStack = new ItemStack(ChangedItems.BLOOD_SYRINGE.get());
            TagUtil.setCustomData(nStack, tag);

            if (!player.addItem(nStack))
                player.drop(nStack, false);
        });
        return stack;
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }
        ChangedSounds.broadcastSound(entity, ChangedSounds.SYRINGE_PRICK, 1, 1);
        if (player != null)
            return usedOnPlayer(stack, level, player, player, false);

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack p_43001_, @Nullable LivingEntity entity) {
        return 16;
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnBlock extends Event implements ICancellableEvent {
        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnBlock(BlockPos blockPos, BlockState blockState, Level level, Player player, ItemStack syringe) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnEntity extends Event implements ICancellableEvent {
        public final LivingEntity entity;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnEntity(LivingEntity entity, Level level, Player player, ItemStack syringe) {
            this.entity = entity;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        return Changed.postModEvent(
                new UsedOnBlock(context.getClickedPos(),
                        clickedState,
                        context.getLevel(),
                        context.getPlayer(),
                        context.getItemInHand())) ?
                InteractionResult.sidedSuccess(context.getLevel().isClientSide) :
                super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof Player interactPlayer) {
            usedOnPlayer(itemStack, interactPlayer.level(), interactPlayer, player, false);
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        return Changed.postModEvent(
                new UsedOnEntity(livingEntity,
                        player.level(),
                        player,
                        itemStack)) ?
                InteractionResult.sidedSuccess(player.level().isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }

    @Nullable
    @Override
    public AnimationHandler getAnimationHandler() {
        return new SyringeAnimation(this);
    }

    public static class SyringeAnimation extends AnimationHandler {
        public SyringeAnimation(Item item) {
            super(item);
        }

        @Override
        public void setupUsingAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model, HumanoidArm arm, float progress) {
            super.setupUsingAnimation(itemStack, entity, model, arm, progress);
            model.pointArmAt(arm, new Vec3(arm == HumanoidArm.RIGHT ? -1 : 1, -0.2, 0.35),
                    Mth.clamp(1.0F - (float)Math.pow(1.0 - progress, 27.0D), 0, 1));
        }

        @Override
        public boolean changesFirstPersonAnimation() {
            return true;
        }

        @Override
        public void setupFirstPersonUseAnimation(ItemStack itemStack, EntityStateContext entity, HumanoidArm arm, PoseStack pose, float progress) {
            super.setupFirstPersonUseAnimation(itemStack, entity, arm, pose, progress);
            float relativeProgress = progress * itemStack.getUseDuration(entity.livingEntity);
            if (progress > 0.2F)
                pose.translate(0.0D, Mth.abs(Mth.cos(relativeProgress / 4.0F * (float)Math.PI) * 0.025F), 0.0D);

            float f3 = 1.0F - (float)Math.pow(1.0 - progress, 27.0D);
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            pose.translate((double)(f3 * 0.3F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
            pose.mulPose(Axis.YP.rotationDegrees((float)i * f3 * 90.0F));
            pose.mulPose(Axis.XP.rotationDegrees(f3 * 10.0F));
            pose.mulPose(Axis.ZP.rotationDegrees((float)i * f3 * 30.0F));
        }
    }

    @Override
    public boolean triggerItemUseEffects(LivingEntity entity, ItemStack itemStack, int particleCount) {
        return true;
    }
}
