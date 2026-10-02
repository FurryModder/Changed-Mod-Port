package net.furrymodder.changed.extension.sodium;

import net.furrymodder.changed.world.LatexCoverState;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface ClonedChunkSectionExtension {
    PalettedContainerRO<LatexCoverState> getLatexCoverData();
}
