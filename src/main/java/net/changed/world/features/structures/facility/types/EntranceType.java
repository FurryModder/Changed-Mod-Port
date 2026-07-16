package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilityEntrance;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EntranceType extends PieceType<FacilityEntrance> {
    public static final MapCodec<FacilityEntrance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityEntrance entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityEntrance entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityEntrance::new));

    @Override
    public MapCodec<FacilityEntrance> getCodec() {
        return CODEC;
    }
}
