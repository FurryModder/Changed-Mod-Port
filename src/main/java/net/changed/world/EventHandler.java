package net.changed.world;

import net.changed.data.AccessorySlots;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public abstract class EventHandler {
    @SubscribeEvent
    public static void onEmptySwingEvent(PlayerInteractEvent.LeftClickEmpty event) {
        AccessorySlots.getForEntity(event.getEntity()).ifPresent(slots -> slots.onEntitySwing(event.getHand()));
    }

    @SubscribeEvent
    public static void onEntityAttackedEvent(LivingIncomingDamageEvent event) {
        AccessorySlots.getForEntity(event.getEntity()).map(slots -> slots.onEntityDamage(event.getSource(), event.getAmount()))
                .ifPresent(event::setAmount);
    }
}
