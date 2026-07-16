package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.entity.latex.LatexType;
import net.changed.init.ChangedAttributes;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexOtter extends ChangedEntity {
    public LatexOtter(EntityType<? extends LatexOtter> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.05);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(1.2);
        attributes.getInstance(ChangedAttributes.AIR_CAPACITY).setBaseValue(60);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#5d4743");
    }
}