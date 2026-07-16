package net.changed.client.renderer.animate.ears;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractEarsAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftEar;
    public final ModelPart rightEar;

    public AbstractEarsAnimator(ModelPart leftEar, ModelPart rightEar) {
        this.leftEar = leftEar;
        this.rightEar = rightEar;
    }
}
