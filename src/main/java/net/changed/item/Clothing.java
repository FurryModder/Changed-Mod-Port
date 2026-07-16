package net.changed.item;

import net.changed.data.AccessorySlotContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface Clothing extends AccessoryItem {
    ArmorMaterial MATERIAL = new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 0, ArmorItem.Type.CHESTPLATE, 0, ArmorItem.Type.LEGGINGS, 0, ArmorItem.Type.BOOTS, 0),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY,
            List.of(),
            0.0F,
            0.0F);

    @Nullable
    default ResourceLocation getTexture(ItemStack stack, Entity entity) {
        return stack.getItem().getArmorTexture(stack, entity, EquipmentSlot.MAINHAND, null, false);
    }

    @Override
    default void accessoryBreak(AccessorySlotContext<?> slotContext) {
        slotContext.stack().hurtAndBreak(1, slotContext.wearer(), EquipmentSlot.MAINHAND);
    }
}
