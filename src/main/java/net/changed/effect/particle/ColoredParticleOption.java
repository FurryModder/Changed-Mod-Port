package net.changed.effect.particle;

import com.mojang.serialization.MapCodec;
import net.changed.util.Color3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ColoredParticleOption implements ParticleOptions {
    public static MapCodec<ColoredParticleOption> codec(ParticleType<ColoredParticleOption> type) {
        return Color3.CODEC.fieldOf("color").xmap(color -> new ColoredParticleOption(type, color), option -> option.color);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ColoredParticleOption> streamCodec(ParticleType<ColoredParticleOption> type) {
        return StreamCodec.of(
                (buffer, option) -> buffer.writeInt(option.color.toInt()),
                buffer -> new ColoredParticleOption(type, Color3.fromInt(buffer.readInt()))
        );
    }

    private final ParticleType<ColoredParticleOption> type;
    private final Color3 color;

    public ColoredParticleOption(ParticleType<ColoredParticleOption> type, Color3 color) {
        this.type = type;
        this.color = color;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public Color3 getColor() {
        return color;
    }
}
