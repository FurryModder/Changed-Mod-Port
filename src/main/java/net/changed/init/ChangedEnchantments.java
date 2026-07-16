package net.changed.init;

import net.changed.Changed;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public class ChangedEnchantments {
    public static final ResourceKey<Enchantment> TRANSFUR_RESISTANCE = key("latex_protection");
    public static final ResourceKey<Enchantment> FORM_FITTING = key("form_fitting");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Changed.modResource(name));
    }

    public static Optional<Holder.Reference<Enchantment>> holder(HolderLookup.Provider registries, ResourceKey<Enchantment> key) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).get(key);
    }

    public static int getEntityLevel(LivingEntity entity, ResourceKey<Enchantment> key) {
        return holder(entity.registryAccess(), key)
                .map(enchantment -> EnchantmentHelper.getEnchantmentLevel(enchantment, entity))
                .orElse(0);
    }

    public static int getItemLevel(HolderLookup.Provider registries, ResourceKey<Enchantment> key, ItemStack stack) {
        return holder(registries, key)
                .map(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack))
                .orElse(0);
    }

    public static int getTagLevel(HolderLookup.Provider registries, ResourceKey<Enchantment> key, ItemStack stack) {
        return holder(registries, key)
                .map(enchantment -> EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack))
                .orElse(0);
    }
}
