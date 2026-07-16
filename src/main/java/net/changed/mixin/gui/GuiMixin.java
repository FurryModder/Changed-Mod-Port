package net.changed.mixin.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.changed.Changed;
import net.changed.client.gui.AbstractRadialScreen;
import net.changed.client.gui.VariantRadialScreen;
import net.changed.process.ProcessTransfur;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Final @Shadow protected Minecraft minecraft;

    @Shadow protected abstract Player getCameraPlayer();

    @Unique private static final ResourceLocation GUI_LATEX_HEARTS = Changed.modResource("textures/gui/latex_hearts.png");
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");
    @Unique private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    @Unique private static final int GUI_LATEX_HEARTS_WIDTH = 256;
    @Unique private static final int GUI_LATEX_HEARTS_HEIGHT = 256;
    @Unique private static final int GUI_HEART_SIZE = 9;

    @Unique
    private static boolean changed$useGoopyHearts() {
        try {
            return Changed.config != null && Changed.config.client.useGoopyHearts.get();
        } catch (IllegalStateException ignored) {
            return false;
        }
    }

    @Unique
    private static boolean changed$isLatexHeartType(Gui.HeartType type) {
        return type == Gui.HeartType.CONTAINER ||
                type == Gui.HeartType.NORMAL ||
                type == Gui.HeartType.ABSORBING ||
                type == Gui.HeartType.POISIONED ||
                type == Gui.HeartType.WITHERED ||
                type == Gui.HeartType.FROZEN;
    }

    @Unique
    private static int changed$heartU(Gui.HeartType type, boolean blinking, boolean half) {
        if (type == Gui.HeartType.CONTAINER)
            return 16 + (blinking ? GUI_HEART_SIZE : 0);
        return 52 + (half ? GUI_HEART_SIZE : 0) + (blinking ? GUI_HEART_SIZE * 2 : 0);
    }

    @Unique
    private static int changed$heartV(boolean hardcore, boolean highlight) {
        int row = hardcore ? GUI_HEART_SIZE * 5 : 0;
        return row + (highlight ? GUI_HEART_SIZE : 0);
    }

    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private void renderHeart(GuiGraphics graphics, Gui.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half,
                             CallbackInfo callback) {
        if (!changed$useGoopyHearts())
            return;
        if (!changed$isLatexHeartType(type))
            return;

        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            if (ProcessTransfur.isPlayerNotLatex(player))
                return;
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                try {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    var colors = VariantRadialScreen.getColors(variant);
                    var color = type == Gui.HeartType.NORMAL ? colors.background() : colors.foreground();
                    int texX = changed$heartU(type, blinking, half);
                    int texY = changed$heartV(hardcore, false);
                    graphics.setColor(color.red(), color.green(), color.blue(), 1);
                    graphics.blit(GUI_LATEX_HEARTS, x, y, texX, texY, GUI_HEART_SIZE, GUI_HEART_SIZE,
                            GUI_LATEX_HEARTS_WIDTH, GUI_LATEX_HEARTS_HEIGHT);
                    graphics.setColor(1, 1, 1, 1);
                    graphics.blit(GUI_LATEX_HEARTS, x, y, texX, changed$heartV(hardcore, true), GUI_HEART_SIZE, GUI_HEART_SIZE,
                            GUI_LATEX_HEARTS_WIDTH, GUI_LATEX_HEARTS_HEIGHT);
                    callback.cancel();
                } finally {
                    graphics.setColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                }
            });
        }
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyInventory.get())
            return;
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerNotLatex(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);

            Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
            if (!collection.isEmpty()) {
                Screen $$4 = this.minecraft.screen;
                if ($$4 instanceof EffectRenderingInventoryScreen) {
                    EffectRenderingInventoryScreen effectrenderinginventoryscreen = (EffectRenderingInventoryScreen) $$4;
                    if (effectrenderinginventoryscreen.canSeeEffects()) {
                        return;
                    }
                }

                RenderSystem.enableBlend();
                int j1 = 0;
                int k1 = 0;
                MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
                List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

                for (MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                    var mobeffect = mobeffectinstance.getEffect();
                    var renderer = net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
                    if (!renderer.isVisibleInGui(mobeffectinstance)) continue;
                    // Rebind in case previous renderHUDEffect changed texture
                        if (mobeffectinstance.showIcon()) {
                        int i = graphics.guiWidth();
                        int j = 1;
                        if (this.minecraft.isDemo()) {
                            j += 15;
                        }

                        if (mobeffect.value().isBeneficial()) {
                            ++j1;
                            i -= 25 * j1;
                        } else {
                            ++k1;
                            i -= 25 * k1;
                            j += 26;
                        }

                        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                        float f = 1.0F;
                        if (mobeffectinstance.isAmbient()) {
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 165 + 512, 166, 24, 24, 768, 256);
                            graphics.setColor(colorPair.background().red(), colorPair.background().green(), colorPair.background().blue(), 1.0F);
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 165, 166, 24, 24, 768, 256);
                        } else {
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 141 + 512, 166, 24, 24, 768, 256);
                            graphics.setColor(colorPair.background().red(), colorPair.background().green(), colorPair.background().blue(), 1.0F);
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 141, 166, 24, 24, 768, 256);
                            if (mobeffectinstance.getDuration() <= 200) {
                                int k = 10 - mobeffectinstance.getDuration() / 20;
                                f = Mth.clamp((float) mobeffectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) mobeffectinstance.getDuration() * (float) Math.PI / 5.0F) * Mth.clamp((float) k / 10.0F * 0.25F, 0.0F, 0.25F);
                            }
                        }
                        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                        TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
                        int l = i;
                        int i1 = j;
                        float f1 = f;
                        list.add(() -> {
                            graphics.setColor(1.0F, 1.0F, 1.0F, f1);
                            graphics.blit(l + 3, i1 + 3, 0, 18, 18, textureatlassprite);
                        });
                        renderer.renderGuiIcon(mobeffectinstance, (Gui)(Object)this, graphics, i, j, 0, f);
                    }
                }

                list.forEach(Runnable::run);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            callback.cancel();
        });
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    protected void renderHotbar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (!variant.getItemUseMode().showHotbar) {
                callback.cancel();
                
                Player player = this.getCameraPlayer();
                if (player != null) {
                    graphics.setColor(1.0F, 1.0F, 1.0F, 0.25F);
                    graphics.blitSprite(HOTBAR_SPRITE, (graphics.guiWidth() / 2) - 91, graphics.guiHeight() - 22, 182, 22);
                    graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        });
    }

    @Inject(method = "renderAirLevel", at = @At("HEAD"), cancellable = true)
    private void hideFullWaterBreatherAir(GuiGraphics graphics, CallbackInfo callback) {
        var player = this.getCameraPlayer();
        if (player == null)
            return;

        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null && variant.breatheMode.canBreatheWater() && variant.getHost().getAirSupply() >= variant.getHost().getMaxAirSupply())
            callback.cancel();
    }

    @WrapOperation(method = "renderAirLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAirSupply()I"))
    private int getScaledAirSupply(Player player, Operation<Integer> original) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(this.getCameraPlayer());
        if (variant == null)
            return original.call(player);

        return (int)(((float)player.getAirSupply() / (float)player.getMaxAirSupply()) * 300.0f);
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderSelectedItemName(GuiGraphics graphics, int yShift, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (!variant.getItemUseMode().showHotbar)
                callback.cancel();
        });
    }
}
