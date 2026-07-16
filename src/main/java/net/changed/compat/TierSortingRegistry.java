package net.changed.compat;

import java.util.List;
import net.minecraft.world.item.Tier;

public final class TierSortingRegistry {
    private TierSortingRegistry() {}

    public static List<Tier> getTiersLowerThan(Tier tier) {
        return List.of();
    }
}
