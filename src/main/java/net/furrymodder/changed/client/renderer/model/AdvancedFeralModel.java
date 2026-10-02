package net.furrymodder.changed.client.renderer.model;

import net.furrymodder.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AdvancedFeralModel<T extends ChangedEntity> extends AdvancedHumanoidModel<T> {
    public AdvancedFeralModel(ModelPart root) {
        super(root);
    }
}
