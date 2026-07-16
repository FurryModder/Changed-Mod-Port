package net.changed.item;

import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlotType;
import net.changed.data.AccessorySlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public interface AccessoryItem {
    public static Predicate<Entity> hasSlotAvailable(ItemStack itemStack) {
        return entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                return AccessorySlots.getForEntity(livingEntity).map(slots -> {
                    return slots.hasSpaceFor(itemStack);
                }).orElse(false);
            }

            return false;
        };
    }

    public static boolean dispenseAccessory(BlockSource source, ItemStack itemStack) {
        BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = source.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(hasSlotAvailable(itemStack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingentity = list.get(0);
            return AccessorySlots.getForEntity(livingentity).map(slots -> {
                var copy = itemStack.copy();
                if (slots.quickMoveStack(itemStack)) {
                    AccessorySlots.equipEventAndSound(livingentity, copy);
                    return true;
                }

                return false;
            }).orElse(false);
        }
    }

    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        protected ItemStack execute(BlockSource source, ItemStack itemStack) {
            return AccessoryItem.dispenseAccessory(source, itemStack) ? itemStack : super.execute(source, itemStack);
        }
    };

    static boolean isEmptyHanded(AccessorySlotContext<?> slotContext, InteractionHand hand) {
        return slotContext.wearer().getItemInHand(hand).isEmpty();
    }

    /**
     * Allows an accessory to deny placement in a slot
     * @param itemStack stack to check
     * @param wearer entity to wear the accessory
     * @param slot slot where the stack will be
     * @return true if the item is allowed in the slot
     */
    default boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return true;
    }

    /**
     * Allows an accessory to declare compatibility with another item
     * @param itemStack stack to check
     * @param otherStack other stack to check, may not be an AccessoryItem
     * @param wearer entity to wear the accessories
     * @param slot slot where this stack will be
     * @param otherSlot slot where the other stack will be
     * @return true if the combination of items is compatible
     */
    default boolean allowedWith(ItemStack itemStack, ItemStack otherStack, LivingEntity wearer, AccessorySlotType slot, AccessorySlotType otherSlot) {
        return true;
    }

    /**
     * Allows equipped accessories to indicate a slot is unavailable
     * @param otherSlot
     * @return
     */
    default boolean shouldDisableSlot(AccessorySlotContext<?> slotContext, AccessorySlotType otherSlot) {
        return false;
    }

    default boolean isConsideredByEnchantment(AccessorySlotContext<?> slotContext, Holder<Enchantment> enchantment) {
        return false;
    }

    default void accessoryEquipped(AccessorySlotContext<?> slotContext) {}
    default void accessoryRemoved(AccessorySlotContext<?> slotContext) {}

    default void accessoryBreak(AccessorySlotContext<?> slotContext) {}
    default void accessoryInteract(AccessorySlotContext<?> slotContext) {}
    default void accessoryTick(AccessorySlotContext<?> slotContext) {}
    default void accessorySwing(AccessorySlotContext<?> slotContext, InteractionHand hand) {}
    default void accessoryAttack(AccessorySlotContext<?> slotContext, InteractionHand hand, Entity target) {}
    default float accessoryHurt(AccessorySlotContext<?> slotContext, DamageSource source, float amount) {
        return amount;
    }
}
