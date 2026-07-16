package net.changed.entity.beast;

import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.init.ChangedAttributes;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexMimicPlant extends LatexLeaf {
    public LatexMimicPlant(EntityType<? extends LatexMimicPlant> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.05);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.95);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#497469");
    }
}
