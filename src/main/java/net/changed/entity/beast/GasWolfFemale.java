package net.changed.entity.beast;

import net.changed.entity.Gender;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class GasWolfFemale extends AbstractGasWolf {
    public GasWolfFemale(EntityType<? extends GasWolfFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}