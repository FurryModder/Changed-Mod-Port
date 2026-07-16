package net.changed.init;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.data.OnlineTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ChangedTextures {
    public static final Map<ResourceLocation, AbstractTexture> REGISTRY = new HashMap<>();

    private static void doOnRenderThread(RenderCall call) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(call);
        else
            call.execute();
    }

    public static ResourceLocation register(ResourceLocation location, Supplier<AbstractTexture> texture) {
        REGISTRY.computeIfAbsent(location, ignored -> texture.get());
        return location;
    }

    public static void lateRegisterTextureNoSave(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, texture.get()));
    }

    public static void lateRegisterTexture(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, REGISTRY.get(register(location, texture))));
    }

    private static void registerAllOnRenderThread() {
        RenderSystem.assertOnRenderThreadOrInit();

        var textureManager = Minecraft.getInstance().getTextureManager();
        REGISTRY.forEach(textureManager::register);
    }

    @SubscribeEvent
    public static void registerAll(FMLClientSetupEvent event) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(ChangedTextures::registerAllOnRenderThread);
        else
            registerAllOnRenderThread();
    }
}
