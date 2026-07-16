package net.changed.client.renderer.animate.arm;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.changed.entity.robot.Exoskeleton;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ArmBobAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public ArmBobAnimator(ModelPart leftArm, ModelPart rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.BOB;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (core.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(rightArm, ageInTicks, 1.0F);
        }

        if (core.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(leftArm, ageInTicks, -1.0F);
        }

        if (Exoskeleton.getEntityExoskeleton(entity.maybeGetUnderlying()).isPresent()) {
            rightArm.zRot += Mth.DEG_TO_RAD * 12f;
            leftArm.zRot += Mth.DEG_TO_RAD * -12f;
        }
    }
}
