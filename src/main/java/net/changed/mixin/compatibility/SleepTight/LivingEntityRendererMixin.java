package net.changed.mixin.compatibility.SleepTight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.entity.ChangedEntity;
import net.changed.extension.RequiredMods;
import net.mehvahdjukaar.sleep_tight.common.entities.BedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
@RequiredMods("sleep_tight")
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "setupRotations", at = @At("HEAD"))
    protected void setupModdedPose(T entity, PoseStack poseStack, float bob, float bodyYRot, float partialTicks, CallbackInfo ci) {
        if (entity instanceof ChangedEntity changedEntity) {
            if (changedEntity.getVehicle() instanceof BedEntity) {
                // OFFSET
                float offsetMultiplier = Changed$getOffsetMultiplier(changedEntity);

                // Apply the translation to the PoseStack
                // Y is kept at 0 unless you need to adjust height relative to the bed
                poseStack.translate(offsetMultiplier, 0.0D, 0);
            }
        }
    }

    @Unique
    private float Changed$getOffsetMultiplier(ChangedEntity changedEntity) {
        float offsetMultiplier;
        switch (changedEntity.getEntityShape()) {
            case ANTHRO -> offsetMultiplier = 0.2f;
            case FERAL -> offsetMultiplier = -1f;
            case NAGA -> offsetMultiplier = -0.08f;
            case MER -> offsetMultiplier = -0.125f;
            case TAUR -> offsetMultiplier = 0.25f;
            default -> offsetMultiplier = 0; // Fail Safe... IDK how this going to trigger but hey better have than don't
        }
        return offsetMultiplier;
    }
}
