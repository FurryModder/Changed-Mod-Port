package net.changed.client.renderer.animate.bipedal;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.entity.ChangedEntity;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class BipedalStandAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalStandAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightLeg.z = 0.1F;
        leftLeg.z = 0.1F;
        rightLeg.y = core.calculateLegPositionY();
        leftLeg.y = core.calculateLegPositionY();
    }
}
