package net.furrymodder.changed.init;

import net.furrymodder.changed.Changed;
import net.furrymodder.changed.effect.Caffeinated;
import net.furrymodder.changed.effect.Confusion;
import net.furrymodder.changed.effect.Hypercoagulation;
import net.furrymodder.changed.effect.Shock;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.furrymodder.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Changed.MODID);

    public static final DeferredHolder<MobEffect, Hypercoagulation> HYPERCOAGULATION = REGISTRY.register("hypercoagulation", Hypercoagulation::new);
    public static final DeferredHolder<MobEffect, Shock> SHOCK = REGISTRY.register("shock", Shock::new);
    public static final DeferredHolder<MobEffect, Confusion> CONFUSION = REGISTRY.register("confusion", Confusion::new);
    public static final DeferredHolder<MobEffect, Caffeinated> CAFFEINATED = REGISTRY.register("caffeinated", Caffeinated::new);
}
