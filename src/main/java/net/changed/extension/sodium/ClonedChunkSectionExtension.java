package net.changed.extension.sodium;

import net.changed.world.LatexCoverState;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface ClonedChunkSectionExtension {
    PalettedContainerRO<LatexCoverState> getLatexCoverData();
}
