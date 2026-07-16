package net.changed.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.changed.ability.GrabEntityAbility;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CommonHooks.class, remap = false)
public abstract class ForgeHooksMixin {
    @WrapOperation(method = "onDamageBlock", at = @At(value = "NEW", target = "net/neoforged/neoforge/event/entity/living/LivingShieldBlockEvent"))
    private static LivingShieldBlockEvent correctBlocker(LivingEntity blocker, DamageContainer container, boolean originalBlockedState, Operation<LivingShieldBlockEvent> op) {
        LivingEntity controller = GrabEntityAbility.getControllingEntity(blocker);
        if (controller == blocker)
            return op.call(blocker, container, originalBlockedState);
        else {
            LivingShieldBlockEvent ev = op.call(controller, container, originalBlockedState);
            ev.setShieldDamage(0.0F);
            return ev;
        }
    }
}
