package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.entity.latex.LatexType;
import net.changed.init.ChangedAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexWhiteTiger extends ChangedEntity implements PowderSnowWalkable {
    public LatexWhiteTiger(EntityType<? extends LatexWhiteTiger> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.15);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(1.1);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(22.0);
        attributes.getInstance(ChangedAttributes.JUMP_STRENGTH).setBaseValue(1.25);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }
}