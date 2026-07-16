package net.changed.init;

import net.changed.Changed;
import net.changed.world.features.structures.ChestLootTableProcessor;
import net.changed.world.features.structures.GluReplacementProcessor;
import net.changed.world.features.structures.HangingBlockFixerProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY_FEATURE = DeferredRegister.create(ForgeRegistries.FEATURES, Changed.MODID);

    public static final DeferredRegister<StructureProcessorType<?>> REGISTRY_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Changed.MODID);
    public static DeferredHolder<StructureProcessorType<?>, StructureProcessorType<ChestLootTableProcessor>> CHEST_LOOT_TABLE_PROCESSOR = REGISTRY_PROCESSOR.register("chest_loot_table_processor",
            () -> () -> ChestLootTableProcessor.CODEC);
    public static DeferredHolder<StructureProcessorType<?>, StructureProcessorType<GluReplacementProcessor>> GLU_REPLACEMENT_PROCESSOR = REGISTRY_PROCESSOR.register("glu_replacement_processor",
            () -> () -> GluReplacementProcessor.CODEC);
    public static DeferredHolder<StructureProcessorType<?>, StructureProcessorType<HangingBlockFixerProcessor>> HANGING_BLOCK_FIXER_PROCESSOR = REGISTRY_PROCESSOR.register("hanging_block_fixer_processor",
            () -> () -> HangingBlockFixerProcessor.CODEC);

    // Defined in changed:worldgen/configured_Feature/orange_tree.json
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Changed.modResource("orange_tree"));
    // TODO: replace orange tree and tree feature mixin with an orange bush.
}
