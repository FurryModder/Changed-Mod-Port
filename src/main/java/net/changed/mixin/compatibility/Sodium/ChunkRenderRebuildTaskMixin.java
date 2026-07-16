package net.changed.mixin.compatibility.Sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.changed.block.LatexCoveringSource;
import net.changed.client.ChangedClient;
import net.changed.extension.RequiredMods;
import net.changed.extension.sodium.OptimizedVertexBuilder;
import net.changed.extension.sodium.WorldSliceExtension;
import net.changed.world.LatexCoverGetter;
import net.changed.world.LatexCoverState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
@RequiredMods("sodium")
public abstract class ChunkRenderRebuildTaskMixin {
    @Unique
    public LatexCoverState getLatexCoverState(LevelSlice slice, BlockPos blockPos) {
        if (!((Object)slice instanceof WorldSliceExtension ext))
            throw new IllegalStateException("WorldSlice not extended");
        return ext.getLatexCoverState(blockPos);
    }

    @Inject(method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Reference2ReferenceOpenHashMap;<init>()V"))
    public void addChangedSteps(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
                                @Local VisGraph occluder) {
        ChunkBuildBuffers buffers = buildContext.buffers;
        LevelSlice slice = buildContext.cache.getWorldSlice();
        BlockAndTintGetter level = (BlockAndTintGetter)slice;
        RandomSource random = RandomSource.create();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
        Map<RenderType, OptimizedVertexBuilder> builderCache = new HashMap<>();

        RenderSection render = ((ChunkBuilderTaskAccessor)(Object)this).changed$getRenderSection();
        int minX = render.getOriginX();
        int minY = render.getOriginY();
        int minZ = render.getOriginZ();
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        for(int y = minY; y < maxY; ++y) {
            if (cancellationToken.isCancelled()) {
                return;
            }

            for (int z = minZ; z < maxZ; ++z) {
                for (int x = minX; x < maxX; ++x) {
                    blockPos.set(x, y, z);

                    BlockState blockState = level.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof LatexCoveringSource source)
                        source.getLatexCoverState(blockState, blockPos);
                    LatexCoverState latexCoverState = getLatexCoverState(slice, blockPos);

                    if (!latexCoverState.isPresent())
                        continue;

                    RenderType rendertype = ChangedClient.latexCoveredBlocksRenderer.get().getRenderType(latexCoverState);
                    Material material = DefaultMaterials.forRenderLayer(rendertype);

                    boolean rendered = ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                            level,
                            LatexCoverGetter.extend(slice, fetchPos -> this.getLatexCoverState(slice, fetchPos)),
                            blockPos,
                            builderCache.computeIfAbsent(rendertype, type -> new OptimizedVertexBuilder(
                                    vertices,
                                    buffers.get(material),
                                    material)),
                            blockState,
                            latexCoverState,
                            random);

                    if (rendered) {
                        blockPos.set(x & 15, y & 15, z & 15);
                        occluder.setOpaque(blockPos);
                    }
                }
            }
        }
    }
}
