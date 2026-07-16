package net.changed.init;

import net.changed.world.LatexCoverState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public class ChangedLootContextParamSets {
    public static final LootContextParamSet LATEX_COVER = register("latex_cover", (builder) -> {
        builder.required(LatexCoverState.LOOT_CONTEXT_PARAM).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS);
    });

    private static LootContextParamSet register(String name, Consumer<LootContextParamSet.Builder> builderConsumer) {
        LootContextParamSet.Builder lootcontextparamset$builder = new LootContextParamSet.Builder();
        builderConsumer.accept(lootcontextparamset$builder);
        return lootcontextparamset$builder.build();
    }
}
