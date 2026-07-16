package net.changed.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CubeDefinitionExtender {
    void removeFaces(Direction... directions);
    @Deprecated
    void copyFaceUVStart(Direction from, Direction to);
    void overrideFaceTexOffs(Direction face, int xOffset, int yOffset);

    @Nullable Map<Direction, Pair<Integer, Integer>> getOverrideFaceTexOffs();
}
