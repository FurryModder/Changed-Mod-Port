package net.changed.item;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.data.AccessorySlotType;
import net.changed.data.AccessorySlots;
import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.ClothingShape;
import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedEnchantments;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public interface ExtendedItemProperties {
    @Nullable
    default SoundEvent getEquipSound(ItemStack itemStack) {
        return SoundEvents.ARMOR_EQUIP_GENERIC.value();
    }

    default SoundEvent getBreakSound(ItemStack itemStack) {
        return SoundEvents.ITEM_BREAK;
    }

    default boolean customWearRenderer(ItemStack itemStack) {
        return false;
    }

    default boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (slot.isArmor())
            return allowedToWear(itemStack, wearer, slot);
        return slot.getType() == EquipmentSlot.Type.HAND;
    }

    // Should only be called with armor slots
    default boolean allowedToWear(ItemStack itemStack, LivingEntity wearer, EquipmentSlot slot) {
        if (!(itemStack.getEquipmentSlot() == slot || (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == slot)))
            return false;
        final EntityShape entityShape = EntityShape.getShapeOf(wearer).orElse(EntityShape.ANTHRO);
        return switch (slot) {
            case HEAD -> entityShape.getHeadShape() == getExpectedHeadShape(itemStack);
            case CHEST -> entityShape.getTorsoShape() == getExpectedTorsoShape(itemStack);
            case LEGS -> entityShape.getLegsShape() == getExpectedLegShape(itemStack);
            case FEET -> entityShape.getFeetShape() == getExpectedFeetShape(itemStack);
            default -> false;
        };
    }

    default ClothingShape.Head getExpectedHeadShape(ItemStack itemStack) {
        return ClothingShape.Head.ANTHRO;
    }

    default ClothingShape.Torso getExpectedTorsoShape(ItemStack itemStack) {
        return ClothingShape.Torso.ANTHRO;
    }

    default ClothingShape.Legs getExpectedLegShape(ItemStack itemStack) {
        return ClothingShape.Legs.BIPEDAL;
    }

    default ClothingShape.Feet getExpectedFeetShape(ItemStack itemStack) {
        return ClothingShape.Feet.BIPEDAL;
    }

    default void wearTick(ItemStack itemStack, LivingEntity wearer) {

    }

    @EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity))
                return;

            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.isArmor())
                            .forEach(slot -> {
                                var itemStack = livingEntity.getItemBySlot(slot);
                                if (itemStack.getItem() instanceof ExtendedItemProperties extended) {
                                    if (!extended.allowedInSlot(itemStack, livingEntity, slot) &&
                                            ChangedEnchantments.getItemLevel(livingEntity.registryAccess(), ChangedEnchantments.FORM_FITTING, itemStack) <= 0) {
                                        livingEntity.setItemSlot(slot, ItemStack.EMPTY);
                                        AccessorySlots.defaultInvalidHandler(livingEntity).accept(itemStack);
                                        return;
                                    }

                                    extended.wearTick(itemStack, livingEntity);
                                }
                            });
        }
    }
}
