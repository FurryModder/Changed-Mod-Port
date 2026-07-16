package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.entity.latex.LatexType;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexGoldenDragon extends ChangedEntity {
    public LatexGoldenDragon(EntityType<? extends LatexGoldenDragon> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#ffdb4f");
    }
}
