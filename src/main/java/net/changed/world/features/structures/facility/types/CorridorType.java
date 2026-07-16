package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedFacilityPieceTypes;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilityCorridorSection;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CorridorType extends PieceType<FacilityCorridorSection> {
    public static final MapCodec<FacilityCorridorSection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityCorridorSection entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityCorridorSection entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityCorridorSection::new));

    @Override
    public MapCodec<FacilityCorridorSection> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canBeReplacedBy(PieceType<?> other) {
        return other == ChangedFacilityPieceTypes.TRANSITION.get() || other == ChangedFacilityPieceTypes.SPLIT.get();
    }
}
