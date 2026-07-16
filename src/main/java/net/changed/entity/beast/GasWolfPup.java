package net.changed.entity.beast;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.entity.TransfurMode;
import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedAttributes;
import net.changed.init.ChangedSounds;
import net.changed.init.ChangedTransfurVariants;
import net.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class GasWolfPup extends GasWolfMale {
    protected static final int MAX_AGE = 72000;
    protected int age = 0;
    public GasWolfPup(EntityType<? extends GasWolfPup> type, Level level) {
        super(type, level);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.25);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.975);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(12.0);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    @Override
    public double getMyRidingOffset() {
        return 0.2;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        age = tag.getInt("age");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("age", age);
    }

    @Override
    protected float getEyeHeightMul() {
        if (this.isCrouching())
            return 0.65F;
        else
            return 0.8F;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.FERAL;
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        age++;

        final int checkAge = ProcessTransfur.ifPlayerTransfurred(getUnderlyingPlayer(), variant -> variant.ageAsVariant, () -> age);
        if (!level.isClientSide &&checkAge > MAX_AGE) {
            IAbstractChangedEntity conversionEntity = IAbstractChangedEntity.forEntity(this);
            var newVariant = ChangedTransfurVariants.Gendered.GAS_WOLVES.getRandomVariant(level().random);

            conversionEntity.replaceVariant(newVariant);
            ChangedSounds.broadcastSound(conversionEntity.getEntity(), newVariant.sound, 1.0f, 1.0f);
            conversionEntity.getEntity().heal(12.0f);
        }
    }

    public boolean canBeLeashed(Player player) {
        return !this.isLeashed();
    }

    @Override
    public boolean isItemAllowedInSlot(ItemStack stack, EquipmentSlot slot) {
        if (slot.isArmor())
            return false;
        return super.isItemAllowedInSlot(stack, slot);
    }
} //TODO: potentially make tameable
