package net.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.changed.client.CubeDefinitionExtender;
import net.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(CubeDefinition.class)
public abstract class CubeDefinitionMixin implements CubeDefinitionExtender {
    @Unique
    private Set<Pair<Direction, Direction>> copyUVStarts = null;
    @Unique
    private Map<Direction, Pair<Integer, Integer>> overrideFaceTexOffs = null;
    @Mutable
    @Shadow @Final private Set<Direction> visibleFaces;

    @Shadow @Final private UVPair texScale;

    @Override
    public void removeFaces(Direction... directions) {
        HashSet<Direction> newFaceSet = new HashSet<>(this.visibleFaces);

        for (var face : directions) {
            // Flip Y-Axis cause models are upside down
            newFaceSet.remove(face.getAxis() == Direction.Axis.Y ? face.getOpposite() : face);
        }

        this.visibleFaces = newFaceSet;
    }

    @Override
    public void copyFaceUVStart(Direction from, Direction to) {
        if (from == to)
            return;
        if (copyUVStarts == null)
            copyUVStarts = new HashSet<>();

        copyUVStarts.add(Pair.of(from, to));
    }

    @Override
    public void overrideFaceTexOffs(Direction face, int xOffset, int yOffset) {
        if (overrideFaceTexOffs == null)
            overrideFaceTexOffs = new HashMap<>();

        overrideFaceTexOffs.put(face, Pair.of(xOffset, yOffset));
    }

    @Override
    public @Nullable Map<Direction, Pair<Integer, Integer>> getOverrideFaceTexOffs() {
        return overrideFaceTexOffs;
    }

    @WrapMethod(method = "bake")
    @SuppressWarnings("deprecation")
    public ModelPart.Cube bakeWithExtra(int texWidth, int texHeight, Operation<ModelPart.Cube> original) {
        var cube = original.call(texWidth, texHeight);

        if (copyUVStarts != null) {
            CubeExtender cubeExtender = (CubeExtender) cube;
            cubeExtender.copyUVStarts(copyUVStarts);
        }

        if (overrideFaceTexOffs != null) {
            CubeExtender cubeExtender = (CubeExtender) cube;
            cubeExtender.overrideFaceTexOffs(overrideFaceTexOffs, texWidth * this.texScale.u(), texHeight * this.texScale.v());
        }

        return cube;
    }
}
