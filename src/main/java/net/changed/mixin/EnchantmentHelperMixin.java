package net.changed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlots;
import net.changed.entity.ChangedEntity;
import net.changed.item.AccessoryItem;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Predicate;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I",
            at = @At("RETURN"), cancellable = true)
    private static void includeChangedFormEnchantments(Holder<Enchantment> enchantment, LivingEntity le,
                                                      CallbackInfoReturnable<Integer> callback) {
        if (enchantment.is(Enchantments.DEPTH_STRIDER)) {
            if (le instanceof ChangedEntity entity) {
                callback.setReturnValue(callback.getReturnValue() + entity.getDepthStriderLevel());
                return;
            }

            ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(le), variant -> {
                callback.setReturnValue(callback.getReturnValue() + variant.getChangedEntity().getDepthStriderLevel());
            });
        } else if (enchantment.is(Enchantments.AQUA_AFFINITY)) {
            ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(le), variant -> {
                if (variant.breatheMode.hasAquaAffinity())
                    callback.setReturnValue(Math.max(callback.getReturnValue(), 1));
            });
        }
    }

    @WrapOperation(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    @SuppressWarnings("unchecked")
    private static Collection<ItemStack> includingAccessorySlots(Map<EquipmentSlot, ItemStack> instance, Operation<Collection<ItemStack>> original,
                                                                 @Local(argsOnly = true) Holder<Enchantment> enchantment,
                                                                 @Local(argsOnly = true) LivingEntity livingEntity) {
        var list = new ArrayList<ItemStack>(Objects.requireNonNullElse(original.call(instance), (Collection<ItemStack>)Collections.EMPTY_LIST));
        AccessorySlots.getForEntity(livingEntity).ifPresent(slots -> {
            slots.forEachSlot((slot, itemStack) -> {
                if (!itemStack.isEmpty() &&
                        itemStack.getItem() instanceof AccessoryItem accessoryItem &&
                        accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(livingEntity, slot, itemStack), enchantment)) {
                    list.add(itemStack);
                }
            });
        });
        return list;
    }

    @Inject(method = "runIterationOnEquipment", at = @At("TAIL"))
    private static void includeAccessorySlotsInEquipmentIteration(LivingEntity livingEntity,
                                                                  EnchantmentHelper.EnchantmentInSlotVisitor visitor,
                                                                  CallbackInfo ci) {
        AccessorySlots.getForEntity(livingEntity).ifPresent(slots -> {
            slots.forEachSlot((slot, itemStack) -> {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof AccessoryItem accessoryItem) {
                    EnchantmentHelper.runIterationOnItem(itemStack, slot.getEquivalentSlot(), livingEntity, (enchantment, level, enchantedItem) -> {
                        if (accessoryItem.isConsideredByEnchantment(new AccessorySlotContext<>(livingEntity, slot, itemStack), enchantment))
                            visitor.accept(enchantment, level, enchantedItem);
                    });
                }
            });
        });
    }
}
