package net.changed.ability.tree;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.changed.Changed;
import net.changed.util.ResourceUtil;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class AbilityTrees extends SimplePreparableReloadListener<Set<AbilityTree>> {
    public static final AbilityTrees INSTANCE = new AbilityTrees();

    private Set<AbilityTree> trees = Set.of();

    private AbilityTree processJSONFile(JsonObject root) {
        return AbilityTree.CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(RuntimeException::new).getFirst();
    }

    @Override
    @NotNull
    public Set<AbilityTree> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new HashSet<>(), resources, "ability_trees", (list, filename, id, json) -> {
            var abilityTree = processJSONFile(json);
            abilityTree.setTreeLocation(id);
            list.add(abilityTree);
        }, (exception, filename) -> Changed.LOGGER.error("Failed to load ability tree from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull Set<AbilityTree> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        trees = output;
    }

    public Set<AbilityTree> getTrees() {
        return trees;
    }
}
