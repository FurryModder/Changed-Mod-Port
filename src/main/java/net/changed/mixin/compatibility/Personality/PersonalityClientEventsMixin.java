package net.changed.mixin.compatibility.Personality;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamabnormals.personality.core.other.PersonalityClientEvents;
import net.changed.extension.RequiredMods;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PersonalityClientEvents.class, remap = false)
@RequiredMods("personality")
public abstract class PersonalityClientEventsMixin {
    @WrapOperation(method = "onEntitySize", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;STANDING_DIMENSIONS:Lnet/minecraft/world/entity/EntityDimensions;", remap = true))
    private static EntityDimensions applyPersonalitySizeEvent(Operation<EntityDimensions> original, @Local(argsOnly = true) EntityEvent.Size event) {
        final var standingDimensions = original.call();
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(event.getEntity()))
                .map(variant -> variant.getTransfurDimensions(Pose.STANDING, standingDimensions))
                .orElse(standingDimensions);
    }
}
