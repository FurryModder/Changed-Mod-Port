package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.changed.world.features.structures.facility.FacilitySplitSection;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SplitType extends PieceType<FacilitySplitSection> {
    public static final MapCodec<FacilitySplitSection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilitySplitSection piece) -> piece.templateName),
            Codec.INT.fieldOf("expected_dependents").orElse(2).forGetter((FacilitySplitSection piece) -> piece.expectedDependents),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilitySplitSection piece) -> piece.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilitySplitSection::new));

    @Override
    public MapCodec<FacilitySplitSection> getCodec() {
        return CODEC;
    }
}
