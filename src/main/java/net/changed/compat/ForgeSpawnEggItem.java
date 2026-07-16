package net.changed.compat;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class ForgeSpawnEggItem extends SpawnEggItem {
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

    public ForgeSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> typeSupplier, int backgroundColor, int highlightColor, Properties properties) {
        super(null, backgroundColor, highlightColor, properties);
        this.typeSupplier = typeSupplier;
    }

    public ForgeSpawnEggItem(EntityType<? extends Mob> type, int backgroundColor, int highlightColor, Properties properties) {
        this(() -> type, backgroundColor, highlightColor, properties);
    }

    @Override
    public EntityType<?> getType(ItemStack stack) {
        EntityType<?> type = typeSupplier.get();
        return type == null ? super.getType(stack) : type;
    }

    @Override
    protected EntityType<?> getDefaultType() {
        EntityType<?> type = typeSupplier.get();
        return type == null ? super.getDefaultType() : type;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        EntityType<?> type = typeSupplier.get();
        return type == null ? FeatureFlagSet.of() : type.requiredFeatures();
    }
}
