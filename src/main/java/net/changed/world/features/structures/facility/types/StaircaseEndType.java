package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.changed.world.features.structures.facility.FacilityStaircaseEnd;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StaircaseEndType extends PieceType<FacilityStaircaseEnd> {
    public static final MapCodec<FacilityStaircaseEnd> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityStaircaseEnd entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityStaircaseEnd entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityStaircaseEnd::new));

    @Override
    public MapCodec<FacilityStaircaseEnd> getCodec() {
        return CODEC;
    }

    @Override
    public boolean shouldConsumeSpan() {
        return false;
    }
}
