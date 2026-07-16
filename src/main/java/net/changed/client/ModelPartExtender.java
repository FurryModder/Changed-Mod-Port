package net.changed.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.RandomSource;

public interface ModelPartExtender {
    void addTriangle(Triangle triangle);

    ModelPart.Cube getRandomCubeWeighted(RandomSource random);
}
