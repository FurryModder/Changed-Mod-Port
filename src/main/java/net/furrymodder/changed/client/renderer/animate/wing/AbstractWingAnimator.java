package net.furrymodder.changed.client.renderer.animate.wing;

import net.furrymodder.changed.client.renderer.animate.HumanoidAnimator;
import net.furrymodder.changed.client.renderer.model.AdvancedHumanoidModel;
import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractWingAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftWing;
    public final ModelPart rightWing;

    public AbstractWingAnimator(ModelPart leftWing, ModelPart rightWing) {
        this.leftWing = leftWing;
        this.rightWing = rightWing;
    }
}
