package net.changed.world.features.structures.facility;

import net.changed.init.ChangedFacilityPieceTypes;
import net.changed.world.features.structures.FacilityPieces;
import net.changed.world.features.structures.LootTables;
import net.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

import java.util.Optional;
import java.util.Set;

public class FacilitySealPiece extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS = WeightedRandomList.create();

    public FacilitySealPiece(ResourceLocation templateName) {
        super(ChangedFacilityPieceTypes.SEAL.get(), templateName, Optional.of(LootTables.LOW_TIER_LAB));
    }

    @Override
    public WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack) {
        return VALID_NEIGHBORS;
    }

    @Override
    public boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents) {
        return true;
    }
}
