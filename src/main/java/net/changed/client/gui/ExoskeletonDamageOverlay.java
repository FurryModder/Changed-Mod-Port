package net.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.changed.Changed;
import net.changed.entity.robot.Exoskeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class ExoskeletonDamageOverlay {
    private static final ResourceLocation TEXTURE = Changed.modResource("textures/misc/exoskeleton_damage_outline.png");

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.CAMERA_OVERLAYS.equals(event.getName()))
            return;

        if (/*event.getType() == RenderGuiOverlayEvent.ElementType.ALL &&*/ Minecraft.getInstance().getCameraEntity() instanceof LivingEntity wearer) {
            var exoOpt = Exoskeleton.getEntityExoskeleton(wearer);
            if (exoOpt.isEmpty())
                return;

            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            float width = Mth.clamp((float)((wearer.hurtTime - 1) + (1f - partialTick)) / (float)wearer.hurtDuration, 0f, 1f);
            if (width <= 0f)
                return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            float uvXLeft = width * 0.5f;
            float uvXRight = 1f - (width * 0.5f);

            bufferbuilder.addVertex(0.0F, screenHeight, -90.0F).setUv(0.0F, 1.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(screenWidth * uvXLeft, screenHeight, -90.0F).setUv(uvXLeft, 1.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(screenWidth * uvXLeft, 0.0F, -90.0F).setUv(uvXLeft, 0.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F).setColor(1f, 1f, 1f, 1f);

            bufferbuilder.addVertex(screenWidth * uvXRight, screenHeight, -90.0F).setUv(uvXRight, 1.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(screenWidth, screenHeight, -90.0F).setUv(1.0F, 1.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F).setColor(1f, 1f, 1f, 1f);
            bufferbuilder.addVertex(screenWidth * uvXRight, 0.0F, -90.0F).setUv(uvXRight, 0.0F).setColor(1f, 1f, 1f, 1f);

            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}
