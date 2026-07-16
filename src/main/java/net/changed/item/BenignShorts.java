package net.changed.item;

import net.changed.Changed;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedSounds;
import net.changed.init.ChangedTransfurVariants;
import net.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class BenignShorts extends ClothingItem implements Shorts, LaserReactiveItem {
    @Nullable
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return Changed.modResource("textures/models/benign_shorts_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
    }

    public static TransfurVariant<?> getBenignTransfurVariant(LivingEntity entity) {
        if (entity.isInWater()) {
            return ChangedTransfurVariants.LATEX_BENIGN_ORCA.get();
        }

        if (entity.getRandom().nextBoolean()) {
            // TODO: return ChangedTransfurVariants.BENIGN_DRAGON.get()
        }

        return ChangedTransfurVariants.LATEX_BENIGN_WOLF.get();
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target, float exposureLevel) {
        return LatexAssimilationDecision.fromBlockOrItem(getBenignTransfurVariant(target), TransfurContext.hazard(TransfurCause.BENIGN_SHORTS), exposureLevel);
    }

    @Override
    public boolean tickLaserExposure(LivingEntity wearer, ItemStack itemStack, float exposureLevel) {
        if (ProcessTransfur.progressTransfur(wearer, this.makeAssimilationDecision(wearer, exposureLevel))) {
            itemStack.shrink(1);
            return true;
        }

        return false;
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
