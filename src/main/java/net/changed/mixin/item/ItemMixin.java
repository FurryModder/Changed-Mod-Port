package net.changed.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.world.LatexCoverGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemLike, net.neoforged.neoforge.common.extensions.IItemExtension {
    @WrapOperation(method = "getPlayerPOVHitResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"))
    private static BlockHitResult extendedPOVHitResult(Level instance, ClipContext clipContext, Operation<BlockHitResult> original) {
        return LatexCoverGetter.wrap(instance).clip(clipContext, original.call(instance, clipContext));
    }
}
