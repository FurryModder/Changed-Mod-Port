package net.changed.world.features.structures.facility;

import net.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class GatherFacilityPiecesEvent extends Event implements IModBusEvent {
    private final PieceType<?> pieceType;
    private final FacilityPieceCollectionBuilder builder;

    public GatherFacilityPiecesEvent(PieceType<?> pieceType, FacilityPieceCollectionBuilder builder) {
        this.pieceType = pieceType;
        this.builder = builder;
    }

    public GatherFacilityPiecesEvent register(ConfiguredFacilityPiece piece) {
        builder.register(piece);
        return this;
    }

    public GatherFacilityPiecesEvent register(ResourceLocation pieceName, FacilityPiece piece) {
        builder.register(pieceName, piece);
        return this;
    }

    public GatherFacilityPiecesEvent register(ResourceLocation pieceName, int weight, FacilityPiece piece) {
        builder.register(pieceName, weight, piece);
        return this;
    }

    public PieceType<?> getPieceType() {
        return pieceType;
    }

    public FacilityPieceCollectionBuilder getBuilder() {
        return builder;
    }
}
