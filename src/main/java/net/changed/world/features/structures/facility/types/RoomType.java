package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.init.ChangedRegistry;
import net.changed.world.features.structures.facility.FacilityRoomPiece;
import net.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RoomType extends PieceType<FacilityRoomPiece> {
    public static final MapCodec<FacilityRoomPiece> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilityRoomPiece entrance) -> entrance.templateName),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter((FacilityRoomPiece entrance) -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityRoomPiece::new));

    @Override
    public MapCodec<FacilityRoomPiece> getCodec() {
        return CODEC;
    }
}
