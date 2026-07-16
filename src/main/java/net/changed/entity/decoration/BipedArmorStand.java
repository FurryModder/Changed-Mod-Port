package net.changed.entity.decoration;

import net.changed.entity.variant.EntityShape;
import net.changed.init.ChangedEntities;
import net.changed.init.ChangedItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BipedArmorStand extends AbstractArmorStand {
    public BipedArmorStand(EntityType<? extends BipedArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    public BipedArmorStand(Level level, double x, double y, double z) {
        this(ChangedEntities.BIPED_ARMOR_STAND.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.ANTHRO;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ChangedItems.BIPED_ARMOR_STAND.get());
    }
}
