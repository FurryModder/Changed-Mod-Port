package net.changed.init;

import net.changed.Changed;
import net.changed.item.loot.RandomVariantFunction;
import net.changed.item.loot.SetVariantFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedLootItemFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> REGISTRY =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Changed.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetVariantFunction>> SET_VARIANT =
            REGISTRY.register("set_variant", () -> new LootItemFunctionType<>(SetVariantFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RandomVariantFunction>> RANDOM_VARIANT =
            REGISTRY.register("random_variant", () -> new LootItemFunctionType<>(RandomVariantFunction.CODEC));
}
