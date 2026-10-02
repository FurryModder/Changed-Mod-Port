package net.furrymodder.changed.mixin.compatibility.SleepTight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.furrymodder.changed.client.renderer.animate.HumanoidAnimator;
import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.extension.RequiredMods;
import net.mehvahdjukaar.sleep_tight.common.entities.BedEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = HumanoidAnimator.AnimateStage.class, remap = false)
@RequiredMods("sleep_tight")
public abstract class HumanoidAnimatorMixin {
    @WrapMethod(method = "isRiding(Lnet/furrymodder/changed/client/renderer/animate/HumanoidAnimator;Lnet/furrymodder/changed/entity/ChangedEntity;)Z")
    private static boolean isRidingAndNotSleeping(HumanoidAnimator<?, ?> animator, ChangedEntity entity, Operation<Boolean> original) {
        if (entity.getVehicle() instanceof BedEntity)
            return false;
        return original.call(animator, entity);
    }

    @WrapMethod(method = "isSleeping(Lnet/furrymodder/changed/client/renderer/animate/HumanoidAnimator;Lnet/furrymodder/changed/entity/ChangedEntity;)Z")
    private static boolean isSleepingAndNotRiding(HumanoidAnimator<?, ?> animator, ChangedEntity entity, Operation<Boolean> original) {
        if (entity.getVehicle() instanceof BedEntity)
            return true;
        return original.call(animator, entity);
    }
}
