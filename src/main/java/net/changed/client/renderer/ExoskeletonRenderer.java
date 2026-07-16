package net.changed.client.renderer;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;
import net.changed.client.renderer.model.ExoskeletonModel;
import net.changed.entity.robot.Exoskeleton;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ExoskeletonRenderer extends MobRenderer<Exoskeleton, ExoskeletonModel> {
    public ExoskeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new ExoskeletonModel(context.bakeLayer(ExoskeletonModel.LAYER_LOCATION_SUIT)), 0.4f);
        this.addLayer(new VisorLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Exoskeleton entity) {
        return model.getTexture(entity);
    }

    @Override
    protected void setupRotations(Exoskeleton exoskeleton, PoseStack poseStack, float bob, float bodyYRot, float partialTicks, float scale) {
        super.setupRotations(exoskeleton, poseStack, bob, bodyYRot, partialTicks, scale);
        if (exoskeleton.isCharging() && exoskeleton.getSleepingPos().isPresent()) {
            poseStack.translate(0.0D, 1 / 16.0, 0.0D);
        }

    }

    @Override
    protected float getFlipDegrees(Exoskeleton exoskeleton) {
        return exoskeleton.isCharging() ? 0.0F : super.getFlipDegrees(exoskeleton);
    }

    private Vec3 getPosition(LivingEntity entity, double eyeHeight, float partialTicks) {
        double d0 = Mth.lerp((double)partialTicks, entity.xOld, entity.getX());
        double d1 = Mth.lerp((double)partialTicks, entity.yOld, entity.getY()) + eyeHeight;
        double d2 = Mth.lerp((double)partialTicks, entity.zOld, entity.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    public void render(Exoskeleton exoskeleton, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float hurtTime = (float)exoskeleton.getHurtTime() - partialTicks;
        float damageTime = exoskeleton.getDamage() - partialTicks;
        if (damageTime < 0.0F) {
            damageTime = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damageTime / 10.0F * (float)exoskeleton.getHurtDir()));
        }

        super.render(exoskeleton, yRot, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        LivingEntity target = exoskeleton.getActiveAttackTarget();
        if (target != null) {
            float attackScale = exoskeleton.getAttackAnimationScale(partialTicks);
            float worldTicks = (exoskeleton.tickCount % 10) + partialTicks;
            float f2 = worldTicks * 0.5F % 1.0F;
            float eyeHeight = exoskeleton.getBbHeight();
            poseStack.pushPose();
            poseStack.translate(0.0D, eyeHeight, 0.0D);
            Vec3 laserTarget = this.getPosition(target, (double)target.getBbHeight() * 0.5D, partialTicks);
            Vec3 laserSource = this.getPosition(exoskeleton, eyeHeight, partialTicks);
            float rotate = (worldTicks / 10f) * Mth.TWO_PI;

            EmittedLaserRenderer.renderBeam(laserSource, laserTarget, 0.125f, rotate, 4f / 16f, poseStack, bufferSource);

            poseStack.popPose();
        }
    }

    public static class VisorLayer extends RenderLayer<Exoskeleton, ExoskeletonModel> {
        private final ExoskeletonModel.VisorModel model;

        public VisorLayer(RenderLayerParent<Exoskeleton, ExoskeletonModel> parent, EntityModelSet modelSet) {
            super(parent);
            this.model = new ExoskeletonModel.VisorModel(modelSet.bakeLayer(ExoskeletonModel.LAYER_LOCATION_VISOR));
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, Exoskeleton entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            model.matchParentAnim(this.getParentModel());
            model.renderToBuffer(pose, bufferSource.getBuffer(model.renderType(model.getTexture(entity))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -1);
        }
    }
}
