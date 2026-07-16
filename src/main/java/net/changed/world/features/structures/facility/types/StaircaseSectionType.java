package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.changed.world.features.structures.facility.FacilityStaircaseSection;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StaircaseSectionType extends PieceType<FacilityStaircaseSection> {
    public static final MapCodec<FacilityStaircaseSection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityStaircaseSection entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityStaircaseSection entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityStaircaseSection::new));

    @Override
    public MapCodec<FacilityStaircaseSection> getCodec() {
        return CODEC;
    }

    @Override
    public boolean shouldConsumeSpan() {
        return false;
    }
}
