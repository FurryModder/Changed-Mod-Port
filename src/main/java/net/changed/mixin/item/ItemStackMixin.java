package net.changed.mixin.item;

import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackExtension {
    @Shadow public abstract Item getItem();

    @Override
    public boolean canEquip(EquipmentSlot armorType, LivingEntity entity) {
        ItemStack self = (ItemStack)(Object)this;
        Player player = EntityUtil.playerOrNull(entity);
        boolean canEquipToSlot = this.getItem().canEquip(self, armorType, entity);
        return ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            return variant.canWear(player, self, armorType) && canEquipToSlot;
        }, () -> canEquipToSlot);
    }

    @Override
    public boolean canElytraFly(LivingEntity entity) {
        ItemStack self = (ItemStack)(Object)this;
        boolean variantCanFly = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity))
                .map(TransfurVariantInstance::canElytraGlide).orElse(false);
        return variantCanFly || this.getItem().canElytraFly(self, entity);
    }

    @Override
    public boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        ItemStack self = (ItemStack)(Object)this;
        boolean variantCanFly = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity))
                .map(TransfurVariantInstance::canElytraGlide).orElse(false);
        return variantCanFly || this.getItem().elytraFlightTick(self, entity, flightTicks);
    }
}
