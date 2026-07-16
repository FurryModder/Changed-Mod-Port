package net.changed.extension.sodium;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ModelCuboidExtender {
    record UVSet(float u0, float v0, float u1, float v1) {}

    @Nullable Map<Direction, UVSet> getOverrideFaceTexOffs();

    void overrideFaceTexOffs(Map<Direction, Pair<Integer, Integer>> overrides, float texWidth, float texHeight, float sizeX, float sizeY, float sizeZ);
}
