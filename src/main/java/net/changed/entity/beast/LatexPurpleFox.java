package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexPurpleFox extends AbstractLatexWolf implements PowderSnowWalkable {
    public LatexPurpleFox(EntityType<? extends LatexPurpleFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.1);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.93);
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#bbbde9");
    }
}
