package net.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.changed.entity.VisionType;
import net.changed.init.ChangedLatexTypes;
import net.changed.process.ProcessTransfur;
import net.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class VariantBlindnessOverlay {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/misc/white.png");
    private static final float ALPHA = 0.45F;
    private static float alphaO = 0.0F;

    public static void render(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (!ProcessTransfur.isPlayerTransfurred(player))
            return;
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return;
        if (player.hasEffect(MobEffects.NIGHT_VISION))
            return; // Override effect
        if (variant.visionType != VisionType.REDUCED)
            return;
        Color3 color = variant.getColors().getFirst();
        var eyePosition = player.getEyePosition(partialTicks);
        float darkness = (15 - player.level().getRawBrightness(new BlockPos(Mth.floor(eyePosition.x), Mth.floor(eyePosition.y), Mth.floor(eyePosition.z)), 0)) / 15.0f;
        float alpha;
        if (variant.getLatexType() == ChangedLatexTypes.DARK_LATEX.get())
            alpha = Mth.lerp(0.65F, alphaO, darkness * ALPHA);
        else
            alpha = ALPHA * Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        alphaO = alpha;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        graphics.setColor(1, 1, 1, 1);
        int i1 = screenWidth;
        int j1 = screenHeight;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(0.0F, j1, -90.0F).setUv(0.0F, 1.0F).setColor(color.red(), color.green(), color.blue(), alpha);
        bufferbuilder.addVertex(i1, j1, -90.0F).setUv(1.0F, 1.0F).setColor(color.red(), color.green(), color.blue(), alpha);
        bufferbuilder.addVertex(i1, 0.0F, -90.0F).setUv(1.0F, 0.0F).setColor(color.red(), color.green(), color.blue(), alpha);
        bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F).setColor(color.red(), color.green(), color.blue(), alpha);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        graphics.setColor(1, 1, 1, 1);
    }
}
