package net.changed.client.renderer.model;

import net.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public interface DoubleHeadedModel<T extends ChangedEntity> {
    ModelPart getHead();
    ModelPart getOtherHead();
}
