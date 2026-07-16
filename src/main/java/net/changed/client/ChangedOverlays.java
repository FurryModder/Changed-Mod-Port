package net.changed.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.changed.Changed;
import net.changed.client.gui.AbilityOverlay;
import net.changed.client.gui.GrabOverlay;
import net.changed.client.gui.TransfurProgressOverlay;
import net.changed.client.gui.VariantBlindnessOverlay;
import net.changed.entity.LivingEntityDataExtension;
import net.changed.fluid.TransfurGas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
public class ChangedOverlays {
    protected static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.parse("textures/misc/vignette.png");

    public static final ResourceLocation DANGER_OVERLAY = Changed.modResource("danger");
    public static final ResourceLocation ABILITY_OVERLAY = Changed.modResource("ability");
    public static final ResourceLocation GRABBED_OVERLAY = Changed.modResource("grabbed");
    public static final ResourceLocation GAS_VFX_OVERLAY = Changed.modResource("gas_vfx");
    public static final ResourceLocation VARIANT_BLINDNESS_OVERLAY = Changed.modResource("variant_blindness");

    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(DANGER_OVERLAY, (graphics, deltaTracker) -> {
            var minecraft = Minecraft.getInstance();
            var gui = minecraft.gui;
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            TransfurProgressOverlay.renderDangerOverlay(gui, graphics, partialTick, screenWidth, screenHeight);
        });
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, ABILITY_OVERLAY, (graphics, deltaTracker) -> {
            var minecraft = Minecraft.getInstance();
            var gui = minecraft.gui;
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            AbilityOverlay.renderSelectedAbility(gui, graphics, partialTick, screenWidth, screenHeight);
        });
        event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, GRABBED_OVERLAY, (graphics, deltaTracker) -> {
            var minecraft = Minecraft.getInstance();
            GrabOverlay.renderProgressBars(minecraft.gui, graphics, deltaTracker.getGameTimeDeltaPartialTick(false),
                    minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        });
        event.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, GAS_VFX_OVERLAY, (graphics, deltaTracker) -> {
            var minecraft = Minecraft.getInstance();
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();
            var cameraEntity = Minecraft.getInstance().cameraEntity;

            if (cameraEntity instanceof LivingEntityDataExtension ext) {
                ext.isEyeInGas(TransfurGas.class).map(TransfurGas::getColor).ifPresent(color -> {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                    graphics.setColor(color.red(), color.green(), color.blue(), 1.0F);

                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    bufferbuilder.addVertex(0.0F, screenHeight, -90.0F).setUv(0.0F, 1.0F);
                    bufferbuilder.addVertex(screenWidth, screenHeight, -90.0F).setUv(1.0F, 1.0F);
                    bufferbuilder.addVertex(screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F);
                    bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
                    BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
                    RenderSystem.depthMask(true);
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                });
            }
        });
        event.registerBelowAll(VARIANT_BLINDNESS_OVERLAY, (graphics, deltaTracker) -> {
            var minecraft = Minecraft.getInstance();
            VariantBlindnessOverlay.render(minecraft.gui, graphics, deltaTracker.getGameTimeDeltaPartialTick(false),
                    minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        });
    }
}
