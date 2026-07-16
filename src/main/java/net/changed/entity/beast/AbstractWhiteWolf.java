package net.changed.entity.beast;

import net.changed.entity.AttributePresets;
import net.changed.entity.GenderedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public abstract class AbstractWhiteWolf extends AbstractLatexWolf implements GenderedEntity {
    public AbstractWhiteWolf(EntityType<? extends AbstractWhiteWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }
}