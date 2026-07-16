package net.changed.client.renderer.animate.bipedal;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.changed.entity.robot.Exoskeleton;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ExoskeletonBipedalAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractBipedalAnimator<T, M> {
    public final ModelPart leftLegLower, leftFoot, leftPad;
    public final ModelPart rightLegLower, rightFoot, rightPad;

    public ExoskeletonBipedalAnimator(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                      ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        super(leftLeg, rightLeg);
        this.leftLegLower = leftLegLower;
        this.leftFoot = leftFoot;
        this.leftPad = leftPad;
        this.rightLegLower = rightLegLower;
        this.rightFoot = rightFoot;
        this.rightPad = rightPad;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (Exoskeleton.getEntityExoskeleton(entity.maybeGetUnderlying()).isEmpty()) return;

        rightLegLower.xRot = Mth.DEG_TO_RAD * 25.0f;
        leftLegLower.xRot = Mth.DEG_TO_RAD * 25.0f;
        rightFoot.xRot = 0f;
        leftFoot.xRot = 0f;

        rightPad.xRot = Mth.DEG_TO_RAD * 20f;
        rightPad.zRot = 0f;
        leftPad.xRot = Mth.DEG_TO_RAD * 20f;
        leftPad.zRot = 0f;
    }
}
