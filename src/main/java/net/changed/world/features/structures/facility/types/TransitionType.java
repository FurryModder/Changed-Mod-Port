package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.changed.world.features.structures.facility.FacilityTransitionSection;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TransitionType extends PieceType<FacilityTransitionSection> {
    public static final MapCodec<FacilityTransitionSection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityTransitionSection entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityTransitionSection entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityTransitionSection::new));

    @Override
    public MapCodec<FacilityTransitionSection> getCodec() {
        return CODEC;
    }

    @Override
    public boolean canBeReplacedBy(PieceType<?> other) {
        return other == this;
    }
}
