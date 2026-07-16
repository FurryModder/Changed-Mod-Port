package net.changed.client.renderer.animate.wing;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractWingAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftWing;
    public final ModelPart rightWing;

    public AbstractWingAnimator(ModelPart leftWing, ModelPart rightWing) {
        this.leftWing = leftWing;
        this.rightWing = rightWing;
    }
}
