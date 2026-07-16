package net.changed.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Set;

public interface CubeExtender {
    Vector3f getVisualMin();
    Vector3f getVisualMax();
    @Deprecated
    void copyUVStarts(Set<Pair<Direction, Direction>> directions);
    void overrideFaceTexOffs(Map<Direction, Pair<Integer, Integer>> overrides, float texWidth, float texHeight);
    ModelPart.Polygon[] getPolygons();

    ModelPart.Polygon getRandomPolygonWeighted(RandomSource random);
}
