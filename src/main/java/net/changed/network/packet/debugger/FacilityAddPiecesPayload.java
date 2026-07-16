package net.changed.network.packet.debugger;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.changed.Changed;
import net.changed.client.ChangedClient;
import net.changed.init.ChangedRegistry;
import net.changed.network.packet.DebuggerPacket;
import net.changed.world.data.ActiveFacilityInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.fml.LogicalSide;
import net.changed.network.legacy.NetworkEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FacilityAddPiecesPayload extends DebuggerPacket.Payload {
    public static final ResourceLocation IDENTIFIER = Changed.modResource("facility/add_pieces");

    public final ResourceLocation dimensionType;
    public final List<ActiveFacilityInstance.PieceGenerationInfo> pieces;

    public FacilityAddPiecesPayload(ServerLevel level, List<ActiveFacilityInstance.PieceGenerationInfo> pieces) {
        super(IDENTIFIER);
        this.dimensionType = level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getKey(level.dimensionType());
        this.pieces = pieces;
    }

    public FacilityAddPiecesPayload(FriendlyByteBuf buffer) {
        super(IDENTIFIER);
        this.dimensionType = buffer.readResourceLocation();
        this.pieces = buffer.readList(elementBuffer -> new ActiveFacilityInstance.PieceGenerationInfo(
                elementBuffer.readResourceLocation(),
                new BoundingBox(elementBuffer.readInt(), elementBuffer.readInt(), elementBuffer.readInt(),
                        elementBuffer.readInt(), elementBuffer.readInt(), elementBuffer.readInt()),
                ChangedRegistry.FACILITY_ZONES.readRegistryObject(buffer)
        ));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.dimensionType);
        buffer.writeCollection(pieces, (elementBuffer, piece) -> {
            elementBuffer.writeResourceLocation(piece.pieceName());
            elementBuffer.writeInt(piece.region().minX());
            elementBuffer.writeInt(piece.region().minY());
            elementBuffer.writeInt(piece.region().minZ());
            elementBuffer.writeInt(piece.region().maxX());
            elementBuffer.writeInt(piece.region().maxY());
            elementBuffer.writeInt(piece.region().maxZ());
            ChangedRegistry.FACILITY_ZONES.writeRegistryObject(buffer, piece.zone());
        });
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                DimensionType dimensiontype = level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).get(dimensionType);
                ChangedClient.debugRenderer.get().facilityDebugRenderer.addPieces(dimensiontype, this.pieces);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
