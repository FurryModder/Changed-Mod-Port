package net.changed.world.features.structures.facility;

import com.google.common.collect.ImmutableList;
import net.changed.data.RegistryElementPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.changed.compat.ForgeRegistries;

import java.util.Set;

public class FacilityPieceCollectionBuilder {
    private final ImmutableList.Builder<ConfiguredFacilityPiece> builder = ImmutableList.builder();

    public static final int WEIGHT_COMMON = 20;
    public static final int WEIGHT_LESSCOMMON = 14;
    public static final int WEIGHT_UNCOMMON = 10;
    public static final int WEIGHT_RARE = 5;
    public static final int WEIGHT_VERY_RARE = 1;

    public FacilityPieceCollectionBuilder register(ConfiguredFacilityPiece piece) {
        builder.add(piece);
        return this;
    }

    public FacilityPieceCollectionBuilder register(ResourceLocation pieceName, FacilityPiece piece) {
        this.register(pieceName, WEIGHT_COMMON, piece);
        return this;
    }

    public FacilityPieceCollectionBuilder register(ResourceLocation pieceName, int weight, FacilityPiece piece) {
        builder.add(new ConfiguredFacilityPiece(piece, Weight.of(weight), 0, 10, Set.of(), RegistryElementPredicate.forAll(ForgeRegistries.BIOMES)).setName(pieceName));
        return this;
    }

    public FacilityPieceCollectionBuilder register(ResourceLocation pieceName, Weight weight, FacilityPiece piece) {
        builder.add(new ConfiguredFacilityPiece(piece, weight, 0, 10, Set.of(), RegistryElementPredicate.forAll(ForgeRegistries.BIOMES)).setName(pieceName));
        return this;
    }

    public FacilityPieceCollectionBuilder registerAll(Iterable<ConfiguredFacilityPiece> pieces) {
        builder.addAll(pieces);
        return this;
    }

    public FacilityPieceCollection build() {
        return new FacilityPieceCollection(builder);
    }
}
