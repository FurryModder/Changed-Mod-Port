package net.changed.item;

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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class LatexFlask extends Item implements VariantHoldingBase {
    public LatexFlask(Properties properties) {
        super(properties);
    }

    @Override
    public Item getOriginalItem() {
        return ChangedBlocks.ERLENMEYER_FLASK.get().asItem();
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

    public Rarity getRarity(ItemStack p_41461_) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, TooltipContext context, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addOwnerTooltip(null, p_43359_, p_43361_);
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    public String getDescriptionId(ItemStack p_43364_) {
        return getOrCreateDescriptionId();
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (player != null) {
            CompoundTag tag = TagUtil.getCustomData(stack);

            if (tag != null && tag.contains("safe") && ProcessTransfur.isPlayerTransfurred(player)) {
                if (tag.getBoolean("safe"))
                    Pale.tryCure(player);
            }

            else if (tag != null && tag.contains("form")) {
                ResourceLocation formLocation = ResourceLocation.parse(tag.getString("form"));
                ProcessTransfur.transfur(entity, ImmediateTransfurDecision.unsafe(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formLocation), TransfurCause.FACE_HAZARD));
            }

            else {
                ProcessTransfur.transfur(entity, ImmediateTransfurDecision.unsafe(ChangedTransfurVariants.FALLBACK_VARIANT.get(), TransfurCause.FACE_HAZARD));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            stack = new ItemStack(ChangedBlocks.ERLENMEYER_FLASK.get());
        }

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack p_43001_, @Nullable LivingEntity entity) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level p_42993_, Player p_42994_, InteractionHand p_42995_) {
        return ItemUtils.startUsingInstantly(p_42993_, p_42994_, p_42995_);
    }
}
