package net.changed.entity.beast;

import net.changed.entity.GenderedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractAquaticGenderedEntity extends AbstractAquaticEntity implements GenderedEntity {
    public AbstractAquaticGenderedEntity(EntityType<? extends AbstractAquaticGenderedEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
}
