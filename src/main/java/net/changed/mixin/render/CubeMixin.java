package net.changed.mixin.render;

import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.client.CubeExtender;
import net.changed.util.Cacheable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Mixin(ModelPart.Cube.class)
public abstract class CubeMixin implements CubeExtender {
    // TODO: This is no longer guaranteed to have 6 faces
    @Shadow @Final private ModelPart.Polygon[] polygons;
    @Shadow @Final public float minX;
    @Shadow @Final public float minY;
    @Shadow @Final public float minZ;
    @Shadow @Final public float maxX;
    @Shadow @Final public float maxY;
    @Shadow @Final public float maxZ;
    @Unique private Cacheable<Vector3f> visualMin = Cacheable.of(() -> {
        Vector3f min = null;
        for (var polygon : this.polygons) {
            for (var vertex : polygon.vertices) {
                if (min == null) {
                    min = new Vector3f(vertex.pos);
                    continue;
                }

                min.x = Math.min(min.x, vertex.pos.x);
                min.y = Math.min(min.y, vertex.pos.y);
                min.z = Math.min(min.z, vertex.pos.z);
            }
        }
        return min;
    });
    @Unique private Cacheable<Vector3f> visualMax = Cacheable.of(() -> {
        Vector3f max = null;
        for (var polygon : this.polygons) {
            for (var vertex : polygon.vertices) {
                if (max == null) {
                    max = new Vector3f(vertex.pos);
                    continue;
                }

                max.x = Math.max(max.x, vertex.pos.x);
                max.y = Math.max(max.y, vertex.pos.y);
                max.z = Math.max(max.z, vertex.pos.z);
            }
        }
        return max;
    });

    @Override
    public Vector3f getVisualMin() {
        return visualMin.get();
    }

    @Override
    public Vector3f getVisualMax() {
        return visualMax.get();
    }

    @Unique
    private static final ModelPart.Vertex NULL_VERTEX = new ModelPart.Vertex(0, 0, 0, 0, 0);

    @Unique
    private ModelPart.Polygon getFaceFromDirection(Direction dir) {
        Vector3f step = dir.getAxis() == Direction.Axis.Y ? dir.getOpposite().step() : dir.step();
        for (ModelPart.Polygon polygon : polygons) {
            if (polygon.normal.dot(step) >= 0.95f)
                return polygon;
        }
        return null;
    }


    @Override
    public void copyUVStarts(Set<Pair<Direction, Direction>> directions) {
        for (var copy : directions) {
            Direction fromDir = copy.getFirst();
            Direction toDir = copy.getSecond();

            if (fromDir == toDir)
                continue;

            ModelPart.Polygon from = getFaceFromDirection(fromDir);
            ModelPart.Polygon to = getFaceFromDirection(toDir);

            if (from == to || from == null || to == null)
                continue;

            for (int i = 0; i < from.vertices.length && i < to.vertices.length; ++i) {
                var fromVtx = from.vertices[i];
                var toVtx = to.vertices[i];

                to.vertices[i] = new ModelPart.Vertex(toVtx.pos, fromVtx.u, fromVtx.v);
            }
        }
    }

    @Override
    public void overrideFaceTexOffs(Map<Direction, Pair<Integer, Integer>> overrides, float texWidth, float texHeight) {
        final float f = 0.0F / texWidth;
        final float f1 = 0.0F / texHeight;

        overrides.forEach((faceDir, offsets) -> {
            ModelPart.Polygon face = getFaceFromDirection(faceDir);
            if (face == null)
                return;

            int oppositeU = 0, oppositeV = 0;
            switch (faceDir) {
                case NORTH, SOUTH -> {
                    oppositeU = offsets.getFirst() + (int)(this.maxX - this.minX);
                    oppositeV = offsets.getSecond() + (int)(this.maxY - this.minY);
                }
                case EAST, WEST -> {
                    oppositeU = offsets.getFirst() + (int)(this.maxZ - this.minZ);
                    oppositeV = offsets.getSecond() + (int)(this.maxY - this.minY);
                }
                case UP, DOWN -> {
                    oppositeU = offsets.getFirst() + (int)(this.maxX - this.minX);
                    oppositeV = offsets.getSecond() + (int)(this.maxZ - this.minZ);
                }
            }

            face.vertices[0] = face.vertices[0].remap(oppositeU / texWidth - f, offsets.getSecond() / texHeight + f1);
            face.vertices[1] = face.vertices[1].remap(offsets.getFirst() / texWidth + f, offsets.getSecond() / texHeight + f1);
            face.vertices[2] = face.vertices[2].remap(offsets.getFirst() / texWidth + f, oppositeV / texHeight - f1);
            face.vertices[3] = face.vertices[3].remap(oppositeU / texWidth - f, oppositeV / texHeight - f1);
        });
    }

    @Override
    public ModelPart.Polygon[] getPolygons() {
        return polygons;
    }

    @Override
    public ModelPart.Polygon getRandomPolygonWeighted(RandomSource random) {
        float[] weights = new float[polygons.length];
        float totalWeight = 0.0f;

        for (int i = 0; i < polygons.length; ++i) {
            var polygon = polygons[i];
            Vector3f min = null, max = null;

            for (var vertex : polygon.vertices) {
                if (min == null)
                    min = new Vector3f(vertex.pos);
                if (max == null)
                    max = new Vector3f(vertex.pos);

                min.x = Math.min(min.x, vertex.pos.x);
                min.y = Math.min(min.y, vertex.pos.y);
                min.z = Math.min(min.z, vertex.pos.z);
                max.x = Math.max(max.x, vertex.pos.x);
                max.y = Math.max(max.y, vertex.pos.y);
                max.z = Math.max(max.z, vertex.pos.z);
            }

            if (min == null) {
                weights[i] = 0.0f;
                continue;
            }

            if (max.x == min.x)
                weights[i] = Math.abs((max.y - min.y) * (max.z - min.z));
            else if (max.y == min.y)
                weights[i] = Math.abs((max.x - min.x) * (max.z - min.z));
            else
                weights[i] = Math.abs((max.x - min.x) * (max.y - min.y));
            totalWeight += weights[i];
        }

        ModelPart.Polygon polygon = null;
        float chosenPolygon = random.nextFloat() * totalWeight;
        for (int i = 0; i < polygons.length; ++i) {
            if (weights[i] <= 0.0f)
                continue;
            if ((chosenPolygon -= weights[i]) > 0.0f)
                continue;

            return polygons[i];
        }

        throw new IndexOutOfBoundsException();
    }
}
