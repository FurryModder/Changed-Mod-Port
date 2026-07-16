package net.changed.world.features.structures.facility;

import com.google.common.collect.ImmutableList;
import net.changed.util.StreamUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;

import java.util.*;
import java.util.stream.Stream;

public class FacilityPieceCollection {
    private final ImmutableList<ConfiguredFacilityPiece> pieces;
    private final int totalWeight;

    FacilityPieceCollection(ImmutableList.Builder<ConfiguredFacilityPiece> pieces) {
        this.pieces = pieces.build();
        this.totalWeight = WeightedRandom.getTotalWeight(this.pieces);
    }

    boolean contains(FacilityPiece facilityPiece) {
        return pieces.stream().map(ConfiguredFacilityPiece::getFacilityPiece).anyMatch(facilityPiece::equals);
    }

    boolean contains(ConfiguredFacilityPiece facilityPiece) {
        return pieces.stream().anyMatch(facilityPiece::equals);
    }

    public Stream<ConfiguredFacilityPiece> stream() {
        return pieces.stream();
    }

    public Stream<ConfiguredFacilityPiece> shuffledStream(RandomSource random) {
        return StreamUtil.weightedShuffledStream(pieces, random, totalWeight);
    }

    public Optional<ConfiguredFacilityPiece> findNextPiece(RandomSource random) {
        if (this.totalWeight == 0) {
            return Optional.empty();
        } else {
            int i = random.nextInt(this.totalWeight);
            return WeightedRandom.getWeightedItem(this.pieces, i);
        }
    }
}
