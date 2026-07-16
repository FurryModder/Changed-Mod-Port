package net.changed.client.renderer.animate.wing;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractArmWingAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftWing;
    public final ModelPart leftSubWing;
    public final ModelPart rightWing;
    public final ModelPart rightSubWing;

    protected AbstractArmWingAnimator(ModelPart leftWing, ModelPart leftSubWing, ModelPart rightWing, ModelPart rightSubWing) {
        this.leftWing = leftWing;
        this.leftSubWing = leftSubWing;
        this.rightWing = rightWing;
        this.rightSubWing = rightSubWing;
    }
}