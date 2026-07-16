package net.changed.mixin.compatibility.Sodium;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.changed.extension.RequiredMods;
import net.changed.extension.sodium.ModelCuboidExtender;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(ModelCuboid.class)
@RequiredMods("sodium")
public abstract class ModelCuboidMixin implements ModelCuboidExtender {
    @Unique
    private Map<Direction, UVSet> overrideFaceTexOffs = null;

    @Override
    public @Nullable Map<Direction, UVSet> getOverrideFaceTexOffs() {
        return overrideFaceTexOffs;
    }

    @Override
    public void overrideFaceTexOffs(Map<Direction, Pair<Integer, Integer>> overrides, float texWidth, float texHeight, float sizeX, float sizeY, float sizeZ) {
        if (overrides == null || overrides.isEmpty())
            return;

        var scaleU = 1.0f / texWidth;
        var scaleV = 1.0f / texHeight;
        overrideFaceTexOffs = new Object2ObjectArrayMap<>(overrides.size());
        overrides.forEach((faceDir, offsets) -> {
            float oppositeU = 0, oppositeV = 0;
            switch (faceDir) {
                case NORTH, SOUTH -> {
                    oppositeU = offsets.getFirst() + sizeX;
                    oppositeV = offsets.getSecond() + sizeY;
                }
                case EAST, WEST -> {
                    oppositeU = offsets.getFirst() + sizeZ;
                    oppositeV = offsets.getSecond() + sizeY;
                }
                case UP, DOWN -> {
                    oppositeU = offsets.getFirst() + sizeX;
                    oppositeV = offsets.getSecond() + sizeZ;
                }
            }
            overrideFaceTexOffs.put(faceDir.getAxis() == Direction.Axis.Y ? faceDir.getOpposite() : faceDir, new UVSet(
                    scaleU * offsets.getFirst(),
                    scaleV * offsets.getSecond(),
                    scaleU * oppositeU,
                    scaleV * oppositeV));
        });
    }
}
