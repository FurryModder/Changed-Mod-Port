package net.changed.world.features.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;

public interface LatexAwareProcessor {
    @Nullable
    StructureTemplateExtender.StructureLatexCoverInfo process(
            LevelReader level,
            BlockPos templatePosition,
            BlockPos bottomCenterPosition,
            StructureTemplateExtender.StructureLatexCoverInfo preProcessedInfo,
            StructureTemplateExtender.StructureLatexCoverInfo postProcessedInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template);

    List<StructureTemplateExtender.StructureLatexCoverInfo> finalizeProcessing(
            ServerLevelAccessor level,
            BlockPos templatePosition,
            BlockPos bottomCenterPosition,
            List<StructureTemplateExtender.StructureLatexCoverInfo> preProcessedInfos,
            List<StructureTemplateExtender.StructureLatexCoverInfo> postProcessedInfos,
            StructurePlaceSettings settings);
}
