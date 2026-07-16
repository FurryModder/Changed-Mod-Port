package net.changed.init;

import net.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.changed.client.renderer.blockentity.PillowRenderer;
import net.changed.client.renderer.blockentity.StasisChamberRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedBlockEntityRenderers {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ChangedBlockEntities.STASIS_CHAMBER.get(), StasisChamberRenderer::new);
            BlockEntityRenderers.register(ChangedBlockEntities.LATEX_CONTAINER.get(), LatexContainerRenderer::new);
            BlockEntityRenderers.register(ChangedBlockEntities.PILLOW.get(), PillowRenderer::new);
        });
    }
}
