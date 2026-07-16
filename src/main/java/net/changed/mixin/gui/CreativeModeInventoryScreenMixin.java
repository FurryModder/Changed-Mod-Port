package net.changed.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.changed.client.gui.BpiButton;
import net.changed.data.AccessorySlots;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow private static CreativeModeTab selectedTab;

    @Unique private boolean buttonClicked;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Unique private Button accessoryButton;

    @Inject(method = "init", at = @At("RETURN"))
    protected void addAccessoryButton(CallbackInfo ci) {
        accessoryButton = this.addRenderableWidget(new BpiButton(this.leftPos - 24, this.height / 2 - 22, (button) -> {
            AbstractContainerMenu backingMenu = ((ItemPickerMenuAccessor)menu).changed$getInventoryMenu();
            if (backingMenu instanceof InventoryMenu invMenu) {
                // Dev note: carried stack isn't tracked on the server in the creative inventory, as opposed to the regular inventory.
                // We need to send the carried stack in the request to open the accessory menu.
                AccessorySlots.openAccessoriesMenu(((InventoryMenuAccessor)invMenu).changed$getOwner(), this.menu.getCarried());
                this.menu.setCarried(ItemStack.EMPTY);
                this.buttonClicked = true;
            }
        }));

        accessoryButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }

    @WrapMethod(method = "mouseReleased")
    protected boolean preventItemThrow(double x, double y, int button, Operation<Boolean> original) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        } else {
            return original.call(x, y, button);
        }
    }

    @Inject(method = "selectTab", at = @At("RETURN"))
    protected void updateAccessoryButtonVisibility(CreativeModeTab tab, CallbackInfo ci) {
        if (tab == null)
            return;
        if (accessoryButton != null)
            accessoryButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }
}
