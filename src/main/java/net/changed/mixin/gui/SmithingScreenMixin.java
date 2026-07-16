package net.changed.mixin.gui;

import net.changed.entity.decoration.AbstractArmorStand;
import net.changed.entity.decoration.CentaurArmorStand;
import net.changed.entity.decoration.LeglessArmorStand;
import net.changed.entity.variant.ClothingShape;
import net.changed.entity.variant.EntityShape;
import net.changed.entity.variant.EntityShapeProvider;
import net.changed.item.ExtendedItemProperties;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin extends Screen {
    @Shadow @Nullable private ArmorStand armorStandPreview;

    protected SmithingScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private ArmorStand createArmorStandForHeadShape(ClothingShape.Head shape) {
        return new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
    }

    @Unique
    private ArmorStand createArmorStandForTorsoShape(ClothingShape.Torso shape) {
        return new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
    }

    @Unique
    private ArmorStand createArmorStandForLegShape(ClothingShape.Legs shape) {
        return switch (shape) {
            case TAIL -> new LeglessArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
            case QUADRUPEDAL -> new CentaurArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
            default -> new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
        };
    }

    @Unique
    private ArmorStand createArmorStandForFeetShape(ClothingShape.Feet shape) {
        return switch (shape) {
            case TAIL -> new LeglessArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
            case QUADRUPEDAL -> new CentaurArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
            default -> new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
        };
    }

    @Inject(method = "updateArmorStandPreview", at = @At("HEAD"))
    private void maybeReplaceArmorStand(ItemStack itemStack, CallbackInfo ci) {
        if (itemStack.getItem() instanceof ExtendedItemProperties ext && itemStack.getItem() instanceof ArmorItem armor) {
            if (!ext.allowedInSlot(itemStack, this.armorStandPreview, armor.getEquipmentSlot())) {
                this.armorStandPreview = switch (armor.getEquipmentSlot()) {
                    case HEAD -> createArmorStandForHeadShape(ext.getExpectedHeadShape(itemStack));
                    case CHEST -> createArmorStandForTorsoShape(ext.getExpectedTorsoShape(itemStack));
                    case LEGS -> createArmorStandForLegShape(ext.getExpectedLegShape(itemStack));
                    case FEET -> createArmorStandForFeetShape(ext.getExpectedFeetShape(itemStack));
                    default -> this.armorStandPreview;
                };

                this.armorStandPreview.setNoBasePlate(true);
                this.armorStandPreview.setShowArms(true);
                this.armorStandPreview.yBodyRot = 210.0F;
                this.armorStandPreview.setXRot(25.0F);
                this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
                this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
            }
        } else {
            if (armorStandPreview instanceof AbstractArmorStand modded) {
                this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
                this.armorStandPreview.setNoBasePlate(true);
                this.armorStandPreview.setShowArms(true);
                this.armorStandPreview.yBodyRot = 210.0F;
                this.armorStandPreview.setXRot(25.0F);
                this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
                this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
            }
        }
    }
}
