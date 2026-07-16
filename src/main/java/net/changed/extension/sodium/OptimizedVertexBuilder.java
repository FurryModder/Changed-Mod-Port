package net.changed.extension.sodium;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;

public class OptimizedVertexBuilder implements VertexConsumer {
    private final ChunkVertexEncoder.Vertex[] vertices;
    private final ChunkModelBuilder wrapped;
    private final Material material;
    private int index = 0;

    public OptimizedVertexBuilder(ChunkVertexEncoder.Vertex[] vertices, ChunkModelBuilder wrapped, Material material) {
        this.vertices = vertices;
        this.wrapped = wrapped;
        this.material = material;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        final var vert = vertices[index];
        vert.x = x;
        vert.y = y;
        vert.z = z;
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        final var vert = vertices[index];
        vert.color = a << 24 | r << 16 | g << 8 | b;
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        final var vert = vertices[index];
        vert.u = u;
        vert.v = v;
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        final var vert = vertices[index];
        vert.light = u | v << 16;
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        if (++index >= vertices.length) {
            final var vertexBuffer = wrapped.getVertexBuffer(ModelQuadFacing.UNASSIGNED);
            vertexBuffer.push(vertices, material);
            index = 0;
        }
        return this;
    }
}
