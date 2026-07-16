package net.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.changed.Changed;
import net.changed.entity.BasicPlayerInfo;
import net.changed.entity.EyeStyle;
import net.changed.network.packet.BasicPlayerInfoPacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BasicPlayerInfoScreen extends Screen {
    private static final int FIELD_WIDTH = 150;
    private static final int FIELD_HEIGHT = 20;
    private static final int COLUMN_GAP = 10;
    private static final int ROW_HEIGHT = 24;
    private static final int CHECKBOX_WIDTH = FIELD_WIDTH * 2 + COLUMN_GAP;

    private final Screen lastScreen;
    private final @Nullable Player player;
    private @Nullable Runnable toolTip = null;

    public BasicPlayerInfoScreen(Screen parent) {
        super(Component.translatable("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = null;
    }

    public BasicPlayerInfoScreen(Screen parent, Player player) {
        super(Component.translatable("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = player;
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    @Override
    public void removed() {
        Changed.config.saveAdditionalData();
        if (this.player != null)
            Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(this.player));
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private int leftColumnX() {
        return this.width / 2 - 155;
    }

    private int controlX(int index) {
        return this.leftColumnX() + (index % 2) * (FIELD_WIDTH + COLUMN_GAP);
    }

    private int controlY(int index) {
        return this.height / 6 + ROW_HEIGHT * (index >> 1);
    }

    @Override
    protected void init() {
        super.init();
        var bpi = Changed.config.client.basicPlayerInfo;
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("changed.config.bpi.hair_color"),
                bpi::getHairColor, bpi::setHairColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("changed.config.bpi.sclera_color"),
                bpi::getScleraColor, bpi::setScleraColor));
        i++;
        var rightIris = this.addRenderableWidget(new ColorSelector(this.font, this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("changed.config.bpi.iris_color.right"),
                bpi::getRightIrisColor, bpi::setRightIrisColor));
        i++;
        var leftIris = this.addRenderableWidget(new ColorSelector(this.font, this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("changed.config.bpi.iris_color.left"),
                bpi::getLeftIrisColor, bpi::setLeftIrisColor));
        this.addRenderableWidget(Button.builder(Component.translatable("changed.config.bpi.iris_color.sync"), button -> {
                    leftIris.setValue(rightIris.getValue());
                    //bpi.setLeftIrisColor(bpi.getRightIrisColor());
                }).bounds(this.controlX(i) + FIELD_WIDTH - 40, this.controlY(i) + ROW_HEIGHT, 40, FIELD_HEIGHT)
                .tooltip(Tooltip.create(Component.translatable("changed.config.bpi.iris_color.sync_tooltip"))).build());
        i++;
        this.addRenderableWidget(Button.builder(Component.translatable("changed.config.bpi.eye_style", bpi.getEyeStyle().getName()), button -> {
            var style = bpi.getEyeStyle();
            int id = style.ordinal();
            if (id < EyeStyle.values().length - 1)
                id += 1;
            else
                id = 0;
            style = EyeStyle.values()[id];
            bpi.setEyeStyle(style);
            button.setMessage(Component.translatable("changed.config.bpi.eye_style", style.getName()));
        }).bounds(this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT).build());
        i += 2;
        this.addRenderableWidget(new AbstractSliderButton(this.controlX(i), this.controlY(i), FIELD_WIDTH, FIELD_HEIGHT, Component.translatable("changed.config.bpi.size"), bpi.getSizeValueForConfiguration(player)) {
            {
                this.updateMessage();
            }

            private double convertToScaledValue() {
                float min = BasicPlayerInfo.getSizeMinimum(player);
                float max = BasicPlayerInfo.getSizeMaximum(player);
                return this.value * (max - min) + min;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("changed.config.bpi.size.value", Math.round(convertToScaledValue() * 100)));
            }

            @Override
            protected void applyValue() {
                bpi.setSize((float)convertToScaledValue());
            }
        });
        i += 2;
        this.addRenderableWidget(new NoWrapCheckbox(this.leftColumnX(), this.controlY(i), CHECKBOX_WIDTH, FIELD_HEIGHT,
                Component.translatable("changed.config.bpi.override_dl_iris"), this.font,
                bpi.isOverrideIrisOnDarkLatex(), bpi::setOverrideIrisOnDarkLatex));
        i += 2;
        this.addRenderableWidget(new NoWrapCheckbox(this.leftColumnX(), this.controlY(i), CHECKBOX_WIDTH, FIELD_HEIGHT,
                Component.translatable("changed.config.bpi.override_all_eye_styles"), this.font,
                bpi.isOverrideOthersToMatchStyle(), bpi::setOverrideOthersToMatchStyle));
        i += 2;

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_96700_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.controlY(i), 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(graphics, p_96563_, p_96564_, p_96565_);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(graphics, p_96563_, p_96564_, p_96565_);
        if (toolTip != null) {
            toolTip.run();
            toolTip = null;
        }
    }

    private static class NoWrapCheckbox extends AbstractButton {
        private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
        private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
        private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
        private static final ResourceLocation CHECKBOX_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox");
        private static final int BOX_SIZE = 17;
        private static final int SPACING = 4;
        private static final int TEXT_COLOR = 0xE0E0E0;

        private final Font font;
        private final Consumer<Boolean> onValueChange;
        private boolean selected;

        private NoWrapCheckbox(int x, int y, int width, int height, Component message, Font font, boolean selected, Consumer<Boolean> onValueChange) {
            super(x, y, width, height, message);
            this.font = font;
            this.selected = selected;
            this.onValueChange = onValueChange;
        }

        @Override
        public void onPress() {
            this.selected = !this.selected;
            this.onValueChange.accept(this.selected);
        }

        @Override
        public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);

            ResourceLocation sprite = this.selected
                    ? (this.isHoveredOrFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE)
                    : (this.isHoveredOrFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE);
            graphics.blitSprite(sprite, this.getX(), this.getY() + (this.height - BOX_SIZE) / 2, BOX_SIZE, BOX_SIZE);

            int textX = this.getX() + BOX_SIZE + SPACING;
            int availableTextWidth = Math.max(0, this.getRight() - textX);
            String label = this.getMessage().getString();
            int textWidth = Math.max(1, this.font.width(label));
            float textScale = availableTextWidth > 0 ? Math.min(1.0F, (float)availableTextWidth / (float)textWidth) : 1.0F;
            int scaledTextHeight = Math.max(1, Math.round(this.font.lineHeight * textScale));
            int textY = this.getY() + (this.height - scaledTextHeight) / 2;

            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().pushPose();
            graphics.pose().translate(textX, textY, 0.0F);
            graphics.pose().scale(textScale, textScale, 1.0F);
            graphics.drawString(this.font, label, 0, 0, TEXT_COLOR);
            graphics.pose().popPose();
        }
    }
}
