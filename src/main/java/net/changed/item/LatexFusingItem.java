package net.changed.item;

import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedSounds;
import net.changed.process.ProcessTransfur;
import net.changed.util.ItemUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

public interface LatexFusingItem extends ExtendedItemProperties {
    TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack);

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
    class Event {
        @SubscribeEvent
        static void onVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
            if (event.isRedundant())
                return;
            if (event.variant == null)
                return;

            final var oldVariant = event.variant;

            ItemUtil.getWearingItems(event.livingEntity).forEach(slottedItem -> {
                if (slottedItem.itemStack().getItem() instanceof LatexFusingItem fusingItem) {
                    var newVariant = fusingItem.getFusionVariant(event.variant, event.livingEntity, slottedItem.itemStack());
                    if (newVariant == null) {
                        return;
                    }
                    slottedItem.itemStack().shrink(1);
                    event.variant = newVariant;
                }
            });

            if (event.variant != oldVariant) {
                ChangedSounds.broadcastSound(event.livingEntity, event.variant.sound, 1, 1);
            }
        }
    }
}
