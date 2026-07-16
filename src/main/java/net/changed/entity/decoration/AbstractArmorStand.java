package net.changed.entity.decoration;

import net.changed.entity.variant.EntityShape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractArmorStand extends ArmorStand implements EntityShape.Provider {
    public AbstractArmorStand(EntityType<? extends AbstractArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    @NotNull
    public abstract EntityShape getEntityShape();
}
