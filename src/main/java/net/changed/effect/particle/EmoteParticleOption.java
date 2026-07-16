package net.changed.effect.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.entity.Emote;
import net.changed.util.UniversalDist;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public class EmoteParticleOption implements ParticleOptions {
    private static Entity entityOrException(int id) {
        return UniversalDist.getLevel().getEntity(id);
    }

    public static MapCodec<EmoteParticleOption> codec(ParticleType<EmoteParticleOption> type) {
        return RecordCodecBuilder.mapCodec(builder -> builder.group(
                Emote.CODEC.fieldOf("emote").forGetter(option -> option.emote),
                Codec.INT.fieldOf("entity").forGetter(option -> option.entity.getId())
        ).apply(builder, (emote, id) -> new EmoteParticleOption(type, emote, entityOrException(id))));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, EmoteParticleOption> streamCodec(ParticleType<EmoteParticleOption> type) {
        return StreamCodec.of(
                (buffer, option) -> {
                    buffer.writeInt(option.emote.ordinal());
                    buffer.writeInt(option.entity.getId());
                },
                buffer -> new EmoteParticleOption(type, Emote.values()[buffer.readInt()], entityOrException(buffer.readInt()))
        );
    }

    private final ParticleType<EmoteParticleOption> type;
    private final Emote emote;
    private final Entity entity;

    public EmoteParticleOption(ParticleType<EmoteParticleOption> type, Emote emote, Entity entity) {
        this.type = type;
        this.emote = emote;
        this.entity = entity;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public Emote getEmote() {
        return emote;
    }

    public Entity getEntity() {
        return entity;
    }
}
