package net.changed.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

public interface WearableBlock {
    EquipmentSlot getEquipmentSlot();
    void wearTick(LivingEntity entity, ItemStack itemStack);

    @EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity))
                return;

            livingEntity.getArmorSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WearableBlock wearableBlock) {
                    wearableBlock.wearTick(livingEntity, itemStack);
                }
            });
        }
    }
}
