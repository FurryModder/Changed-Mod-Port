package net.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.changed.Changed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BpiButton extends Button {
    private static final Component TITLE = Component.translatable("changed.config.bpi.screen");
    private static final ResourceLocation TEXTURE = Changed.modResource("textures/gui/basic_player_info.png");
    private static final int SIZE = 20;

    public BpiButton(int x, int y, Button.OnPress onPress) {
        super(x, y, SIZE, SIZE, TITLE, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int v = this.isHoveredOrFocused() ? SIZE : 0;
        graphics.blit(TEXTURE, this.getX(), this.getY(), 0, v, SIZE, SIZE, SIZE, SIZE * 2);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
