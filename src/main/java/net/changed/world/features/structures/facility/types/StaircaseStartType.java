package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.changed.world.features.structures.facility.FacilityStaircaseStart;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StaircaseStartType extends PieceType<FacilityStaircaseStart> {
    public static final MapCodec<FacilityStaircaseStart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityStaircaseStart entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityStaircaseStart entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityStaircaseStart::new));

    @Override
    public MapCodec<FacilityStaircaseStart> getCodec() {
        return CODEC;
    }
}
