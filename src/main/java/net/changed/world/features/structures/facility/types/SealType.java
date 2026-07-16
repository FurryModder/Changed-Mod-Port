package net.changed.world.features.structures.facility.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.world.features.structures.facility.FacilitySealPiece;
import net.minecraft.resources.ResourceLocation;

public class SealType extends PieceType<FacilitySealPiece> {
    public static final MapCodec<FacilitySealPiece> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter((FacilitySealPiece entrance) -> entrance.templateName)
    ).apply(instance, FacilitySealPiece::new));

    @Override
    public MapCodec<FacilitySealPiece> getCodec() {
        return CODEC;
    }
}
