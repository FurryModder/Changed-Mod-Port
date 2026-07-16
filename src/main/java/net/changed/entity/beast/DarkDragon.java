package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.entity.latex.LatexType;
import net.changed.init.ChangedLatexTypes;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class DarkDragon extends ChangedEntity implements DarkLatexEntity {
    public DarkDragon(EntityType<? extends ChangedEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean isMaskless() {
        return true;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.DARK_LATEX.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }
}
