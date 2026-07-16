package net.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.block.StasisChamber;
import net.changed.client.LivingEntityRendererExtender;
import net.changed.client.renderer.accessory.WornExoskeletonRenderer;
import net.changed.client.renderer.layers.AccessoryLayer;
import net.changed.client.tfanimations.TransfurAnimator;
import net.changed.entity.robot.Exoskeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> implements LivingEntityRendererExtender<T, M> {
    @Shadow protected abstract boolean isBodyVisible(T p_115341_);

    @Shadow public abstract M getModel();

    @Shadow @Final public List<RenderLayer<T, M>> layers;

    @Shadow @Nullable protected abstract RenderType getRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_);

    @Shadow protected abstract float getWhiteOverlayProgress(T p_115334_, float p_115335_);

    @Shadow protected M model;

    @Shadow protected abstract float getAttackAnim(T p_115343_, float p_115344_);

    @Unique private final List<RenderLayer<T, M>> backupLayers = new ArrayList<>();

    @Unique
    private void prepareLayers(T entity) {
        if (!TransfurAnimator.isCapturing())
            return;

        backupLayers.addAll(layers);
        layers.clear();
        backupLayers.stream().filter(TransfurAnimator::isLayerAllowed).forEach(layers::add);
    }

    @Unique
    private void unprepareLayers(T entity) {
        if (!TransfurAnimator.isCapturing())
            return;

        layers.clear();
        layers.addAll(backupLayers);
        backupLayers.clear();
    }

    @Override
    public void directRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.prepareLayers(entity);

        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        this.model.riding = false;
        this.model.young = entity.isBaby();
        this.model.setupAnim(entity, 0.0f, 0.0f, entity.tickCount + partialTicks, 0.0f, 0.0f);

        boolean bodyVisible = this.isBodyVisible(entity);
        boolean shouldBeVisible = !bodyVisible && !entity.isInvisibleTo(Minecraft.getInstance().player);
        boolean shouldGlow = Minecraft.getInstance().shouldEntityAppearGlowing(entity);
        var renderType = this.getRenderType(entity, bodyVisible, shouldBeVisible, shouldGlow);
        if (renderType != null) {
            int overlay = LivingEntityRenderer.getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            this.model.renderToBuffer(poseStack, bufferSource.getBuffer(renderType), packedLight, overlay, -1);
        }

        for (var layer : this.layers) {
            layer.render(poseStack, bufferSource, packedLight, entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0f);
        }

        this.unprepareLayers(entity);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    public void beforeRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        this.prepareLayers(entity);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    public void afterRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        this.unprepareLayers(entity);
    }

    @Unique
    private boolean changed$hasPoseForStasisRender(LivingEntity entity, Pose pose, Operation<Boolean> original) {
        if (pose == Pose.SLEEPING && StasisChamber.isEntityStabilized(entity))
            return false;

        return original.call(entity, pose);
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z"))
    private boolean renderStasisChamberAsCenteredPose(LivingEntity entity, Pose pose, Operation<Boolean> original) {
        return changed$hasPoseForStasisRender(entity, pose, original);
    }

    @WrapOperation(method = "setupRotations",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z", ordinal = 0))
    private boolean setupStasisChamberInitialRotationAsCenteredPose(LivingEntity entity, Pose pose, Operation<Boolean> original) {
        return changed$hasPoseForStasisRender(entity, pose, original);
    }

    @WrapOperation(method = "setupRotations",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z", ordinal = 1))
    private boolean setupStasisChamberSleepRotationAsCenteredPose(LivingEntity entity, Pose pose, Operation<Boolean> original) {
        return changed$hasPoseForStasisRender(entity, pose, original);
    }

    @Inject(method = "setupRotations", at = @At("HEAD"))
    protected void setupModdedPose(T entity, PoseStack poseStack, float p_115319_, float p_115320_, float p_115321_, float scale, CallbackInfo ci) {
        Exoskeleton.getEntityExoskeleton(entity).ifPresent(pair -> {
            AccessoryLayer.getRenderer(pair.getSecond()).ifPresent(renderer -> {
                if (renderer instanceof WornExoskeletonRenderer exoRenderer) {
                    exoRenderer.getModel().animateWearerPose(entity, this.model, poseStack, pair.getFirst());
                }
            });
        });
    }
}
