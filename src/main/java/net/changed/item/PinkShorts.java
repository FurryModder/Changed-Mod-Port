package net.changed.item;

import net.changed.Changed;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedEntities;
import net.changed.init.ChangedSounds;
import net.changed.init.ChangedTransfurVariants;
import net.changed.process.ProcessTransfur;
import net.changed.util.TagUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class PinkShorts extends ClothingItem implements LatexFusingItem {
    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return Changed.modResource("textures/models/pink_shorts_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
    }

    @Override
    public TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack) {
        if (livingEntity.level().isClientSide)
            return currentVariant;

        if (currentVariant.is(ChangedTransfurVariants.LATEX_DEER))
            return ChangedTransfurVariants.LATEX_PINK_DEER.get();
        else if (currentVariant.is(ChangedTransfurVariants.LATEX_YUIN))
            return ChangedTransfurVariants.LATEX_PINK_YUIN_DRAGON.get();
        else {
            if (livingEntity.getRandom().nextBoolean()) {
                var newEntity = currentVariant.getEntityType().create(livingEntity.level());
                newEntity.moveTo(livingEntity.position());
                livingEntity.level().addFreshEntity(newEntity);
                return ChangedTransfurVariants.LATEX_PINK_WYVERN.get();
            } else {
                var wyvern = ChangedEntities.LATEX_PINK_WYVERN.get().create(livingEntity.level());
                wyvern.moveTo(livingEntity.position());
                livingEntity.level().addFreshEntity(wyvern);
                return currentVariant; // Return current to consume pants (Yummy)
            }
        }
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.LATEX_PINK_WYVERN.get(), TransfurContext.hazard(TransfurCause.PINK_SHORTS), 3.0f);
    }

    @Override
    public void wearTick(ItemStack itemStack, LivingEntity wearer) {
        var tag = TagUtil.getOrCreateCustomData(itemStack);
        var age = (tag.contains("age") ? tag.getInt("age") : 0) + 1;
        TagUtil.updateCustomData(itemStack, itemTag -> itemTag.putInt("age", age));
        if (age < 12000) // Half a minecraft day
            return;
        if (ProcessTransfur.progressTransfur(wearer, this.makeAssimilationDecision(wearer)))
            itemStack.shrink(1);
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.SHORTS_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SHORTS_BREAK.get();
    }
}
