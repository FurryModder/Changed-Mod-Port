package net.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.changed.util.Cacheable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class TscVestItem extends ClothingItem {
    protected static final UUID VEST_ARMOR_UUID = UUID.fromString("07cd149c-e5ee-4355-9ebd-3bbc8e02e3e8");

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
}
