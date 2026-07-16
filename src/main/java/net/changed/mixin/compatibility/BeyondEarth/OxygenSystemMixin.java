package net.changed.mixin.compatibility.BeyondEarth;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.st0x0ef.beyond_earth.common.util.OxygenSystem;
import net.changed.extension.RequiredMods;
import net.changed.fluid.TransfurGas;
import net.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = OxygenSystem.class, remap = false)
@RequiredMods("beyond_earth")
public abstract class OxygenSystemMixin {
    @WrapMethod(method = "canBreatheWithoutSuit")
    private static OxygenSystem.AirCheckResult changed$maybeUseVariant(LivingEntity entity, boolean applyChunkO2, Operation<OxygenSystem.AirCheckResult> original) {
        return original.call(EntityUtil.maybeGetOverlaying(entity), applyChunkO2);
    }

    @WrapOperation(method = "canBreatheWithoutSuit", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/FluidType;isAir()Z"))
    private static boolean changed$isGasAir(FluidType instance, Operation<Boolean> original,
                                            @Local(argsOnly = true) LivingEntity entity) {
        return TransfurGas.validEntityInGas(entity).map(gas -> false).orElse(original.call(instance));
    }
}
