package net.changed.mixin.compatibility.Sodium;

import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.PalettedContainerROExtension;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.caffeinemc.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.changed.block.LatexCoveringSource;
import net.changed.extension.RequiredMods;
import net.changed.extension.sodium.ClonedChunkSectionExtension;
import net.changed.extension.sodium.WorldSliceExtension;
import net.changed.init.ChangedLatexTypes;
import net.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Objects;

@Mixin(value = LevelSlice.class, remap = false)
@RequiredMods("sodium")
public abstract class WorldSliceMixin implements WorldSliceExtension {
    private static final LatexCoverState EMPTY_LATEX_COVER_STATE = ChangedLatexTypes.NONE.get().defaultCoverState();

    @Shadow @Final private static int SECTION_ARRAY_SIZE;
    @Shadow @Final private static int SECTION_ARRAY_LENGTH;

    @Shadow private int originBlockX;
    @Shadow private int originBlockY;
    @Shadow private int originBlockZ;

    @Shadow public abstract BlockState getBlockState(int x, int y, int z);

    @Unique
    private static int changed$getLocalBlockIndex(int x, int y, int z) {
        return (y << 8) | (z << 4) | x;
    }

    @Unique
    private static int changed$getLocalSectionIndex(int x, int y, int z) {
        return y * SECTION_ARRAY_LENGTH * SECTION_ARRAY_LENGTH + z * SECTION_ARRAY_LENGTH + x;
    }

    @Unique private final LatexCoverState[][] latexCoverStatesArrays = new LatexCoverState[SECTION_ARRAY_SIZE][4096];

    @Unique
    private void unpackLatexCoverData(LatexCoverState[] states, ChunkRenderContext context, ClonedChunkSection section) {
        if (((ClonedChunkSectionExtension)section).getLatexCoverData() == null) {
            Arrays.fill(states, EMPTY_LATEX_COVER_STATE);
        } else {
            PalettedContainerROExtension<LatexCoverState> container = PalettedContainerROExtension.of(((ClonedChunkSectionExtension)section).getLatexCoverData());
            SectionPos origin = context.getOrigin();
            SectionPos pos = section.getPosition();
            if (origin.equals(pos)) {
                container.sodium$unpack(states);
            } else {
                BoundingBox bounds = context.getVolume();
                int minBlockX = Math.max(bounds.minX(), pos.minBlockX());
                int maxBlockX = Math.min(bounds.maxX(), pos.maxBlockX());
                int minBlockY = Math.max(bounds.minY(), pos.minBlockY());
                int maxBlockY = Math.min(bounds.maxY(), pos.maxBlockY());
                int minBlockZ = Math.max(bounds.minZ(), pos.minBlockZ());
                int maxBlockZ = Math.min(bounds.maxZ(), pos.maxBlockZ());
                container.sodium$unpack(states, minBlockX & 15, minBlockY & 15, minBlockZ & 15, maxBlockX & 15, maxBlockY & 15, maxBlockZ & 15);
            }

        }
    }

    @Override
    public LatexCoverState getLatexCoverState(int x, int y, int z) {
        final BlockState blockState = getBlockState(x, y, z);
        if (blockState.getBlock() instanceof LatexCoveringSource source)
            return source.getLatexCoverState(blockState, new BlockPos(x, y, z));

        int relX = x - this.originBlockX;
        int relY = y - this.originBlockY;
        int relZ = z - this.originBlockZ;
        int sectionIndex = changed$getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4);
        int blockIndex = changed$getLocalBlockIndex(relX & 15, relY & 15, relZ & 15);
        if (sectionIndex < 0 || sectionIndex >= SECTION_ARRAY_SIZE)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        if (blockIndex < 0 || blockIndex >= 4096)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        return Objects.requireNonNullElseGet(this.latexCoverStatesArrays[sectionIndex][blockIndex],
                () -> ChangedLatexTypes.NONE.get().defaultCoverState());
    }

    @Inject(method = "copySectionData", at = @At("TAIL"))
    public void copyChangedSectionData(ChunkRenderContext context, int sectionIndex, CallbackInfo ci) {
        ClonedChunkSection section = context.getSections()[sectionIndex];

        try {
            this.unpackLatexCoverData(this.latexCoverStatesArrays[sectionIndex], context, section);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Exception copying block data for section: " + section.getPosition(), e);
        }
    }
}
