package net.changed.world.features.structures.facility;

import net.changed.block.entity.GluBlockEntity;
import net.changed.init.ChangedRegistry;
import net.changed.util.TagUtil;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * The position of a glu block
 */
public record GenStep(StructureTemplate.StructureBlockInfo blockInfo, WeightedRandomList<WeightedPieceNeighborSupplier> validTypes) {
    public Zone getZone() {
        if (blockInfo.nbt() != null && blockInfo.nbt().contains(GluBlockEntity.ZONE))
            return ChangedRegistry.FACILITY_ZONES.getValue(TagUtil.getResourceLocation(blockInfo.nbt(), GluBlockEntity.ZONE));
        return null;
    }
}
