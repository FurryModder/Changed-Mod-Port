package net.changed.world.features.structures;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedStructureTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;

import java.util.Optional;

public class Beehive extends Structure {
    public static final MapCodec<Beehive> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                settingsCodec(instance),
                ResourceLocation.CODEC.fieldOf("piece").forGetter(Beehive::getPiece)
        ).apply(instance, Beehive::new);
    });

    private final ResourceLocation piece;

    public Beehive(Structure.StructureSettings settings, ResourceLocation piece) {
        super(settings);
        this.piece = piece;
    }

    public ResourceLocation getPiece() {
        return piece;
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        builder.addPiece(new SurfaceNBTPiece(this.getPiece(), null, context));
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int i = context.chunkPos().getBlockX(9);
        int j = context.chunkPos().getBlockZ(9);

        for(Holder<Biome> holder : context.biomeSource().getBiomesWithin(i, context.chunkGenerator().getSeaLevel(), j, 6, context.randomState().sampler())) {
            if (holder.is(BiomeTags.IS_OCEAN) || holder.is(BiomeTags.IS_RIVER)) {
                return Optional.empty();
            }
        }
        
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
            generatePieces(builder, context);
        });
    }

    @Override
    public StructureType<?> type() {
        return ChangedStructureTypes.BEEHIVE.get();
    }
}
