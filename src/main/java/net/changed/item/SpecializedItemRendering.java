package net.changed.item;

import net.changed.compat.ForgeRegistries;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SpecializedItemRendering {
    static boolean isGUI(ItemDisplayContext type) {
        return type == ItemDisplayContext.GUI || type == ItemDisplayContext.GROUND || type == ItemDisplayContext.FIXED;
    }

    @Nullable ResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type);
    void loadSpecialModels(Consumer<ResourceLocation> loader);

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    class Event {
        @SubscribeEvent
        public static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (item instanceof SpecializedItemRendering specialized)
                    specialized.loadSpecialModels(model -> event.register(ModelResourceLocation.standalone(model)));
            });
        }
    }
}
