package net.changed.client.renderer.model;

import net.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public interface TripleHeadedModel<T extends ChangedEntity> extends DoubleHeadedModel<T> {
    ModelPart getCenterHead();
}
