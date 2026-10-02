package net.furrymodder.changed.client.latexparticles;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.init.ChangedRegistry;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class LatexParticleType<T extends LatexParticle> {
    public static final DeferredRegister<LatexParticleType<?>> REGISTRY = ChangedRegistry.LATEX_PARTICLE_TYPE.createDeferred(Changed.MODID);

    public LatexParticleType() {

    }

    public static final DeferredHolder<LatexParticleType<?>, LatexParticleType<LatexDripParticle>> LATEX_DRIP_PARTICLE = REGISTRY.register("dripping_latex_new", LatexParticleType::new);
}
