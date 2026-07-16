package net.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.changed.entity.ChangedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Optional;

public final class InfuserRecipePreviewRenderer {
    private static Preview queuedPreview;

    private InfuserRecipePreviewRenderer() {
    }

    public static void beginFrame() {
        queuedPreview = null;
    }

    public static void renderOrQueue(GuiGraphics graphics, Minecraft minecraft, int centerX, int bottomY,
                                     int scale, float angleX, float angleY,
                                     ChangedEntity entity, boolean hovered, List<Component> tooltip) {
        if (minecraft.getEntityRenderDispatcher().getRenderer(entity) == null) {
            return;
        }

        Preview preview = new Preview(centerX, bottomY, scale, angleX, angleY, entity, hovered, List.copyOf(tooltip));
        if (hovered) {
            queuedPreview = preview;
        } else {
            preview.render(graphics);
        }
    }

    public static void renderQueued(GuiGraphics graphics) {
        if (queuedPreview != null) {
            RenderSystem.clear(256, Minecraft.ON_OSX);
            RenderSystem.enableDepthTest();
            queuedPreview.render(graphics);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    public static void renderQueuedTooltip(GuiGraphics graphics, Minecraft minecraft, int mouseX, int mouseY) {
        if (queuedPreview != null) {
            RenderSystem.disableDepthTest();
            graphics.renderTooltip(minecraft.font, queuedPreview.tooltip(), Optional.empty(), mouseX, mouseY);
            queuedPreview = null;
        }
    }

    private record Preview(int centerX, int bottomY, int scale, float angleX, float angleY,
                           ChangedEntity entity, boolean hovered, List<Component> tooltip) {
        private void render(GuiGraphics graphics) {
            float entityScale = entity.getScale();
            float scaledHeight = scale * entity.getBbHeight() / entityScale;
            float scaledWidth = scale * Math.max(entity.getBbWidth(), entity.getBbHeight()) / entityScale;
            int halfWidth = Math.max(hovered ? 100 : 24, Mth.ceil(scaledWidth + (hovered ? 72.0F : 16.0F)));
            int halfHeight = Math.max(hovered ? 140 : 36, Mth.ceil(scaledHeight + (hovered ? 72.0F : 16.0F)));
            float bottomOffset = scale * (entity.getBbHeight() / (2.0F * entityScale) + 0.0625F);
            int centerY = Math.round(bottomY - bottomOffset);

            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, hovered ? 300.0F : 0.0F);
            InventoryScreen.renderEntityInInventoryFollowsAngle(
                    graphics,
                    centerX - halfWidth,
                    centerY - halfHeight,
                    centerX + halfWidth,
                    centerY + halfHeight,
                    scale,
                    0.0625F,
                    angleX,
                    angleY,
                    entity);
            graphics.pose().popPose();
        }
    }
}
