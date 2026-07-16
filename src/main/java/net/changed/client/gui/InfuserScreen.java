package net.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.Changed;
import net.changed.item.Syringe;
import net.changed.network.packet.SyncSwitchPacket;
import net.changed.world.inventory.InfuserMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class InfuserScreen extends AbstractContainerScreen<InfuserMenu> implements RecipeUpdateListener {
    public static class Switch extends AbstractButton {
        private static final int TEXT_COLOR = 14737632;
        public boolean disabled;
        private boolean toggle;
        private final boolean showLabel;
        public final AbstractContainerScreen<?> containerScreen;
        private final ResourceLocation name;

        public void onPress() {
            if (this.disabled)
                return;
            this.toggle = !this.toggle;

            Changed.PACKET_HANDLER.sendToServer(SyncSwitchPacket.of(this));
        }

        public boolean selected() {
            return this.toggle;
        }

        private final ResourceLocation sheet;

        public Switch(AbstractContainerScreen<?> container, ResourceLocation name,
                      int p_93826_, int p_93827_, int p_93828_, int p_93829_, Component p_93830_, boolean p_93831_, ResourceLocation sheet) {
            super(p_93826_, p_93827_, p_93828_, p_93829_, p_93830_);
            this.name = name;
            this.containerScreen = container;
            this.sheet = sheet;
            this.toggle = p_93831_;
            this.showLabel = true;
        }

        public void renderWidget(GuiGraphics graphics, int p_93844_, int p_93845_, float p_93846_) {
            super.renderWidget(graphics, p_93844_, p_93845_, p_93846_);
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            int switchX = this.isHoveredOrFocused() ? this.width : 0;
            int switchY = this.disabled ? this.height * 2 : (this.toggle ? this.height : 0);
            graphics.blit(sheet, this.getX(), this.getY(), switchX, switchY, this.width, this.height, this.width * 2, this.height * 3);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput p_259858_) {
            this.defaultButtonNarrationText(p_259858_);
        }

        public ResourceLocation getName() {
            return name;
        }
    }

    private static final ResourceLocation RECIPE_BUTTON_LOCATION = ResourceLocation.parse("textures/gui/recipe_button.png");
    private static final ResourceLocation GENDER_SWITCH_LOCATION = Changed.modResource("textures/gui/gender_switch.png");
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;
    private Switch maleFemaleSwitch;

    public InfuserScreen(InfuserMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 16, this.height / 2 - 30, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, (p_98484_) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            p_98484_.setPosition(this.leftPos + 16, this.height / 2 - 30);
            maleFemaleSwitch.setPosition(this.leftPos + 135, this.topPos + 61);
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.titleLabelX = 5;
        this.titleLabelY = 5;

        maleFemaleSwitch = new Switch(this, Changed.modResource("male_female_switch"), this.leftPos + 135, this.topPos + 61, 20, 10, Component.empty(), false,
                GENDER_SWITCH_LOCATION);
        this.addRenderableWidget(maleFemaleSwitch);
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/infuser.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, int p_98480_, int p_98481_, float p_98482_) {
        InfuserRecipePreviewRenderer.beginFrame();

        var variant = Syringe.getVariant(menu.getResultSlot().getItem());
        maleFemaleSwitch.disabled = variant != null && !variant.isGendered();

        this.renderBackground(graphics, p_98480_, p_98481_, p_98482_);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(graphics, p_98482_, p_98480_, p_98481_);
            this.recipeBookComponent.render(graphics, p_98480_, p_98481_, p_98482_);
        } else {
            this.recipeBookComponent.render(graphics, p_98480_, p_98481_, p_98482_);
            super.render(graphics, p_98480_, p_98481_, p_98482_);
            this.recipeBookComponent.renderGhostRecipe(graphics, this.leftPos, this.topPos, true, p_98482_);
        }

        this.renderTooltip(graphics, p_98480_, p_98481_);
        this.recipeBookComponent.renderTooltip(graphics, this.leftPos, this.topPos, p_98480_, p_98481_);
        InfuserRecipePreviewRenderer.renderQueued(graphics);
        InfuserRecipePreviewRenderer.renderQueuedTooltip(graphics, this.minecraft, p_98480_, p_98481_);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int gx, int gy) {
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public @NotNull RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}
