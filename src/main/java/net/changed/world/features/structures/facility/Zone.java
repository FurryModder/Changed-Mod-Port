package net.changed.world.features.structures.facility;

import net.changed.data.RegistryElementPredicate;
import net.changed.init.ChangedRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.changed.compat.IForgeRegistry;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Zone {
    private final ResourceLocation name;
    private final boolean unique;
    private final int minimumLength;
    private final RegistryElementPredicate<Biome> surfaceBiomePredicate;

    public Zone(ResourceLocation name, boolean unique, int minimumLength, RegistryElementPredicate<Biome> surfaceBiomePredicate) {
        this.name = name;
        this.unique = unique;
        this.minimumLength = minimumLength;
        this.surfaceBiomePredicate = surfaceBiomePredicate;
    }

    public int getMinimumLength() {
        return minimumLength;
    }

    public static BiFunction<IForgeRegistry<Biome>, ResourceLocation, Zone> anyBiome(boolean unique, int minimumLength) {
        return predicatedBiome(unique, minimumLength, RegistryElementPredicate::forAll);
    }

    public static BiFunction<IForgeRegistry<Biome>, ResourceLocation, Zone> taggedBiome(boolean unique, int minimumLength, TagKey<Biome> biomeTag) {
        return predicatedBiome(unique, minimumLength, biomeRegistry -> RegistryElementPredicate.forTag(biomeRegistry, biomeTag));
    }

    public static BiFunction<IForgeRegistry<Biome>, ResourceLocation, Zone> predicatedBiome(boolean unique, int minimumLength, Function<IForgeRegistry<Biome>, RegistryElementPredicate<Biome>> predicateFunction) {
        return (biomeRegistry, name) -> new Zone(name, unique, minimumLength, predicateFunction.apply(biomeRegistry));
    }

    public static Zone random(RandomSource r) {
        var values = ChangedRegistry.FACILITY_ZONES.get().getValues();
        AtomicInteger index = new AtomicInteger(r.nextInt(values.size()));

        return ChangedRegistry.FACILITY_ZONES.get().getValues().stream().filter(
                zone -> index.getAndDecrement() == 0
        ).findAny().orElse(null);
    }

    public static Zone findNext(Zone previous) {
        var list = List.copyOf(ChangedRegistry.FACILITY_ZONES.get().getValues());
        int index = list.indexOf(previous);
        if (index == -1)
            throw new IllegalArgumentException("Zone is not registered");
        if (index + 1 >= list.size())
            return list.get(0);
        return list.get(index + 1);
    }

    public Component getTranslatedName() {
        return Component.translatable("facility.zone." + this.name);
    }

    public boolean canConnectTo(Zone other) {
        return this == other;
    }

    public RegistryElementPredicate<Biome> getSurfaceBiomePredicate() {
        return surfaceBiomePredicate;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    public boolean isUnique() {
        return unique;
    }
}