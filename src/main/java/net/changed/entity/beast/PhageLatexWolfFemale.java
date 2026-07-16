package net.changed.entity.beast;

import net.changed.entity.Gender;
import net.changed.entity.TransfurMode;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class PhageLatexWolfFemale extends AbstractDarkLatexWolf {
    public PhageLatexWolfFemale(EntityType<? extends PhageLatexWolfFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
