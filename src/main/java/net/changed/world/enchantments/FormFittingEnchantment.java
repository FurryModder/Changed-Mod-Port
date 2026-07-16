package net.changed.world.enchantments;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.ClothingShape;
import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedEnchantments;
import net.changed.item.ExtendedItemProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.changed.compat.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class FormFittingEnchantment {
    private static Stream<ArmorItem> getArmors(net.minecraft.core.Holder<net.minecraft.world.item.ArmorMaterial> material, EquipmentSlot slot) {
        return ForgeRegistries.ITEMS.getValues().stream().map(item -> {
            if (item instanceof ArmorItem armor)
                return armor;
            else
                return null;
        }).filter(Objects::nonNull)
                .filter(armorItem -> armorItem.getEquipmentSlot() == slot)
                .filter(armorItem -> armorItem.getMaterial().is(material));
    }

    private static @Nullable ItemStack findEquivalentItemForHeadShape(ClothingShape.Head shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedHeadShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Head.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.HEAD).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedHeadShape(pseudo) == shape) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Head.DEFAULT) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForTorsoShape(ClothingShape.Torso shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedTorsoShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Torso.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.CHEST).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedTorsoShape(pseudo) == shape) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Torso.DEFAULT) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForLegsShape(ClothingShape.Legs shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedLegShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Legs.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.LEGS).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedLegShape(pseudo) == shape) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Legs.DEFAULT) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private static @Nullable ItemStack findEquivalentItemForFeetShape(ClothingShape.Feet shape, ItemStack itemStack) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && ext.getExpectedFeetShape(itemStack) == shape)
            return itemStack;
        else if (!(itemStack.getItem() instanceof ExtendedItemProperties) && shape == ClothingShape.Feet.DEFAULT)
            return itemStack;
        if (!(itemStack.getItem() instanceof ArmorItem armorItem))
            return null;

        return getArmors(armorItem.getMaterial(), EquipmentSlot.FEET).map(candidate -> {
            ItemStack pseudo = new ItemStack(candidate, itemStack.getCount());
            if (candidate instanceof ExtendedItemProperties ext && ext.getExpectedFeetShape(pseudo) == shape) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            } else if (!(candidate instanceof ExtendedItemProperties) && shape == ClothingShape.Feet.DEFAULT) {
                pseudo.applyComponents(itemStack.getComponentsPatch());
                return pseudo;
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static @NotNull ItemStack getFormFitted(LivingEntity wearer, ItemStack itemStack, EquipmentSlot slot) {
        if (!slot.isArmor())
            return itemStack;
        if (ChangedEnchantments.getTagLevel(wearer.registryAccess(), ChangedEnchantments.FORM_FITTING, itemStack) <= 0)
            return itemStack;

        final ItemStack equivalent = EntityShape.getShapeOf(wearer)
                .map(shape -> {
                    return switch (slot) {
                        case HEAD -> findEquivalentItemForHeadShape(shape.headShape, itemStack);
                        case CHEST -> findEquivalentItemForTorsoShape(shape.torsoShape, itemStack);
                        case LEGS -> findEquivalentItemForLegsShape(shape.legsShape, itemStack);
                        case FEET -> findEquivalentItemForFeetShape(shape.feetShape, itemStack);
                        default -> null;
                    };
                }).orElseGet(() -> {
                    return switch (slot) {
                        case HEAD -> findEquivalentItemForHeadShape(ClothingShape.Head.DEFAULT, itemStack);
                        case CHEST -> findEquivalentItemForTorsoShape(ClothingShape.Torso.DEFAULT, itemStack);
                        case LEGS -> findEquivalentItemForLegsShape(ClothingShape.Legs.DEFAULT, itemStack);
                        case FEET -> findEquivalentItemForFeetShape(ClothingShape.Feet.DEFAULT, itemStack);
                        default -> null;
                    };
                });

        return equivalent == null ? itemStack : equivalent;
    }
}
