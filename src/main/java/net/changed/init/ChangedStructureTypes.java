package net.changed.init;

import net.changed.Changed;
import net.changed.world.features.structures.Beehive;
import net.changed.world.features.structures.DecayedLab;
import net.changed.world.features.structures.Facility;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedStructureTypes {
    public static DeferredRegister<StructureType<?>> REGISTRY = DeferredRegister.create(Registries.STRUCTURE_TYPE, Changed.MODID);

    public static DeferredHolder<StructureType<?>, StructureType<Beehive>> BEEHIVE = REGISTRY.register("beehive", () -> () -> Beehive.CODEC);
    public static DeferredHolder<StructureType<?>, StructureType<DecayedLab>> DECAYED_LAB = REGISTRY.register("decayed_lab", () -> () -> DecayedLab.CODEC);
    public static DeferredHolder<StructureType<?>, StructureType<Facility>> FACILITY = REGISTRY.register("facility", () -> () -> Facility.CODEC);
}
