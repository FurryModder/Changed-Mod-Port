package net.changed.init;

import com.mojang.serialization.MapCodec;
import net.changed.Changed;
import net.changed.effect.particle.*;
import net.changed.entity.Emote;
import net.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChangedParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Changed.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOption>> DRIPPING_LATEX = register("dripping_latex",
            ColoredParticleOption::codec, ColoredParticleOption::streamCodec);
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOption>> GAS = register("gas",
            ColoredParticleOption::codec, ColoredParticleOption::streamCodec);
    public static final DeferredHolder<ParticleType<?>, ParticleType<EmoteParticleOption>> EMOTE = register("emote",
            EmoteParticleOption::codec, EmoteParticleOption::streamCodec);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TSC_SWEEP_ATTACK = REGISTRY.register("tsc_sweep_attack",
            () -> new SimpleParticleType(false));

    public static ColoredParticleOption drippingLatex(Color3 color) {
        return new ColoredParticleOption(DRIPPING_LATEX.get(), color);
    }

    public static ColoredParticleOption gas(Color3 color) {
        return new ColoredParticleOption(GAS.get(), color);
    }

    public static EmoteParticleOption emote(Entity entity, Emote emote) {
        return new EmoteParticleOption(EMOTE.get(), emote, entity);
    }

    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            String name,
            Function<ParticleType<T>, MapCodec<T>> codec,
            Function<ParticleType<T>, StreamCodec<RegistryFriendlyByteBuf, T>> streamCodec
    ) {
        return REGISTRY.register(name, () -> new ParticleType<T>(false) {
            @Override
            public MapCodec<T> codec() {
                return codec.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec.apply(this);
            }
        });
    }
}
