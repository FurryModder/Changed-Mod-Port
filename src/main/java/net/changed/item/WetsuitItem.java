package net.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlotType;
import net.changed.data.AccessorySlots;
import net.changed.init.ChangedAccessorySlots;
import net.changed.init.ChangedSounds;
import net.changed.util.Cacheable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.UUID;

public class WetsuitItem extends ClothingItem {
    protected static final UUID WETSUIT_SWIM_SPEED_UUID = UUID.fromString("6799bab3-9457-4a0c-a7e2-743b4134b9b0");
    protected static final UUID WETSUIT_MOVEMENT_SPEED_UUID = UUID.fromString("2bcb6d9e-1c9f-41ff-b61c-e2df363e3e58");

    private static final Cacheable<Multimap<Attribute, AttributeModifier>> DEFAULT_MODIFIERS = Cacheable.of(ImmutableMultimap::of);

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return DEFAULT_MODIFIERS.get();
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) {
            return getAttributeModifiers(stack);
        }

        return ImmutableMultimap.of();
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return super.allowedInSlot(itemStack, wearer, slot) &&
                AccessorySlots.isSlotAvailable(wearer, ChangedAccessorySlots.LEGS.get());
    }

    @Override
    public boolean shouldDisableSlot(AccessorySlotContext<?> slotContext, AccessorySlotType otherSlot) {
        return super.shouldDisableSlot(slotContext, otherSlot) || otherSlot == ChangedAccessorySlots.LEGS.get();
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.WETSUIT_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.WETSUIT_BREAK.get();
    }
}
