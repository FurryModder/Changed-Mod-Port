package net.changed.world.features.structures.facility;

import net.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

import java.util.function.Supplier;

public record WeightedPieceNeighborSupplier(Supplier<? extends PieceType<?>> pieceType, int weight) implements WeightedEntry {
    @Override
    public Weight getWeight() {
        return Weight.of(weight);
    }

    public PieceType<?> getPieceType() {
        return pieceType.get();
    }

    public static WeightedPieceNeighborSupplier of(Supplier<? extends PieceType<?>> pieceType, int weight) {
        return new WeightedPieceNeighborSupplier(pieceType, weight);
    }
}
