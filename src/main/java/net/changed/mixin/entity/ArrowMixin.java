package net.changed.mixin.entity;

import net.changed.init.ChangedItems;
import net.changed.item.Syringe;
import net.changed.util.TagUtil;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.changed.item.LatexTippedArrowItem.FORM_LOCATION;

@Mixin(AbstractArrow.class)
public abstract class ArrowMixin {
    @Inject(method = "getPickupItem", at = @At("HEAD"), cancellable = true)
    protected void getPickupItem(CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if ((Object)this instanceof Arrow arrow && arrow.getPersistentData().contains(FORM_LOCATION)) {
            ItemStack itemStack = new ItemStack(ChangedItems.LATEX_TIPPED_ARROW.get());
            Syringe.setUnpureVariant(itemStack, TagUtil.getResourceLocation(arrow.getPersistentData(), FORM_LOCATION));
            callbackInfoReturnable.setReturnValue(itemStack);
        }
    }
}
