package net.changed.compat;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ForgeRegistries {
    public static final ForgeRegistry<Block> BLOCKS = ForgeRegistry.of(BuiltInRegistries.BLOCK);
    public static final ForgeRegistry<Item> ITEMS = ForgeRegistry.of(BuiltInRegistries.ITEM);
    public static final ForgeRegistry<EntityType<?>> ENTITY_TYPES = ForgeRegistry.of(BuiltInRegistries.ENTITY_TYPE);
    public static final ForgeRegistry<Attribute> ATTRIBUTES = ForgeRegistry.of(BuiltInRegistries.ATTRIBUTE);
    public static final ForgeRegistry<MobEffect> MOB_EFFECTS = ForgeRegistry.of(BuiltInRegistries.MOB_EFFECT);
    public static final ForgeRegistry<ParticleType<?>> PARTICLE_TYPES = ForgeRegistry.of(BuiltInRegistries.PARTICLE_TYPE);
    public static final ForgeRegistry<SoundEvent> SOUND_EVENTS = ForgeRegistry.of(BuiltInRegistries.SOUND_EVENT);
    public static final ForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = ForgeRegistry.of(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final ForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = ForgeRegistry.of(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE);
    public static final ForgeRegistry<Feature<?>> FEATURES = ForgeRegistry.of(BuiltInRegistries.FEATURE);
    public static final ForgeRegistry<Fluid> FLUIDS = ForgeRegistry.of(BuiltInRegistries.FLUID);
    public static final ForgeRegistry<MenuType<?>> MENU_TYPES = ForgeRegistry.of(BuiltInRegistries.MENU);
    public static final ForgeRegistry<RecipeType<?>> RECIPE_TYPES = ForgeRegistry.of(BuiltInRegistries.RECIPE_TYPE);
    public static final ForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ForgeRegistry.of(BuiltInRegistries.RECIPE_SERIALIZER);
    public static final ForgeRegistry<FluidType> FLUID_TYPES = ForgeRegistry.of(NeoForgeRegistries.FLUID_TYPES);

    public static final ForgeRegistry<Biome> BIOMES = ForgeRegistry.empty(Registries.BIOME);
    public static final ForgeRegistry<Enchantment> ENCHANTMENTS = ForgeRegistry.empty(Registries.ENCHANTMENT);
    public static final ForgeRegistry<PaintingVariant> PAINTING_VARIANTS = ForgeRegistry.empty(Registries.PAINTING_VARIANT);

    public static final class Keys {
        public static final net.minecraft.resources.ResourceKey<Registry<FluidType>> FLUID_TYPES = NeoForgeRegistries.Keys.FLUID_TYPES;
    }

    private ForgeRegistries() {}
}
