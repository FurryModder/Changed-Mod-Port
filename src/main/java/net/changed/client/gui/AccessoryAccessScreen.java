package net.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.Changed;
import net.changed.data.AccessorySlotContext;
import net.changed.data.AccessorySlotType;
import net.changed.data.AccessorySlots;
import net.changed.item.AccessoryItem;
import net.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AccessoryAccessScreen extends EffectRenderingInventoryScreen<AccessoryAccessMenu> {
    private final AccessoryAccessMenu menu;
    private float xMouse;
    private float yMouse;
    private int textureWidth;
    private int textureHeight;
    private @Nullable Runnable toolTip = null;

    public AccessoryAccessScreen(AccessoryAccessMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.menu = menu;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.textureWidth = imageWidth + 18;
        this.textureHeight = imageHeight;
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/accessories.png");

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);

        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        this.renderTooltip(graphics, mouseX, mouseY);

        if (toolTip != null) {
            if (this.menu.getCarried().isEmpty()) {
                toolTip.run();
            }
            toolTip = null;
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int gx, int gy) {
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.textureWidth, this.textureHeight);

        AtomicInteger slotIndex = new AtomicInteger(0);
        menu.getBuiltSlots().forEach(slotType -> {
            var slot = menu.getCustomSlot(slotIndex.getAndAdd(1));
            if (slot == null)
                return;

            final List<ItemStack> conflictingItems = menu.getBuiltSlots().stream()
                    .filter(otherSlotType -> otherSlotType != slotType)
                    .<ItemStack>map(otherSlotType -> {
                        final var context = AccessorySlotContext.of(menu.owner, otherSlotType);
                        if (context.stack().getItem() instanceof AccessoryItem accessoryItem &&
                                accessoryItem.shouldDisableSlot(context, slotType))
                            return context.stack();
                        return null;
                    }).filter(Objects::nonNull).toList();

            if (conflictingItems.isEmpty())
                graphics.blit(texture, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, this.imageWidth, 0, 18, 18, this.textureWidth, this.textureHeight);
            else {
                graphics.blit(texture, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, this.imageWidth, 18, 18, 18, this.textureWidth, this.textureHeight);

                if (slot == this.hoveredSlot) {
                    setToolTip(() -> {
                        final var rows = new ArrayList<Component>();
                        rows.add(Component.translatable("changed.accessory.slot_disabled_by"));
                        conflictingItems.forEach(stack -> rows.add(stack.getHoverName()));

                        graphics.renderTooltip(this.font, rows, Optional.empty(), gx, gy);
                    });
                }
            }
        });

        RenderSystem.disableBlend();

        int i = this.leftPos;
        int j = this.topPos;
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
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
    protected void slotClicked(@Nullable Slot slot, int index, int button, @NotNull ClickType clickType) {
        if (slot != null && button == this.minecraft.options.keyUse.getKey().getValue()) {
            if (slot.container instanceof AccessorySlots accessorySlots) {
                AccessorySlotType slotType = accessorySlots.getSlotTypeByIndex(slot.getSlotIndex());
                if (slotType != null) {
                    AccessorySlots.onInteractAccessory(menu.owner, slotType);
                    return;
                }
            }
        }

        super.slotClicked(slot, index, button, clickType);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
