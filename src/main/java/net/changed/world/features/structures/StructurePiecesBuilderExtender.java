package net.changed.world.features.structures;

import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public interface StructurePiecesBuilderExtender {
    boolean removePiece(StructurePiece piece);
    int pieceCount();

    List<StructurePiece> getPieces();
}
