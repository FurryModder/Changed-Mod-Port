package net.changed.datagen;

import net.changed.Changed;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class DatapackEntriesProvider extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DatapackEntriesProvider::damageType)
            .add(Registries.BIOME, DatapackEntriesProvider::biome)
            ;

    public DatapackEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Changed.MODID));
    }

    private static void damageType(BootstapContext<DamageType> context) {
    }

    private static void biome(BootstapContext<Biome> context) {
    }
}