package net.furrymodder.changed.client.renderer.model;

import net.furrymodder.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public interface TripleHeadedModel<T extends ChangedEntity> extends DoubleHeadedModel<T> {
    ModelPart getCenterHead();
}
