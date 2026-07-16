package net.changed.init;

import net.changed.Changed;
import net.changed.data.DeferredStateProvider;
import net.changed.data.MixedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.changed.compat.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChangedBlockStateProviders {
    public static final DeferredRegister<BlockStateProviderType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, Changed.MODID);
    public static final DeferredHolder<BlockStateProviderType<?>, BlockStateProviderType<DeferredStateProvider>> DEFERRED_STATE_PROVIDER
            = REGISTRY.register("deferred_state_provider", () -> new BlockStateProviderType<>(DeferredStateProvider.CODEC));
    public static final DeferredHolder<BlockStateProviderType<?>, BlockStateProviderType<MixedStateProvider>> MIXED_STATE_PROVIDER
            = REGISTRY.register("mixed_state_provider", () -> new BlockStateProviderType<>(MixedStateProvider.CODEC));
}
