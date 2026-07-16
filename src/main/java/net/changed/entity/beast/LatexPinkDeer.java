package net.changed.entity.beast;

import net.changed.entity.LatexTypeOld;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.entity.latex.LatexType;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexPinkDeer extends LatexPinkWyvern {
    public LatexPinkDeer(EntityType<? extends LatexPinkDeer> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.95);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        if (cause == TransfurCause.PINK_SHORTS)
            return Color3.fromInt(0xd8bc99);
        else
            return Color3.fromInt(0xf7aebe);
    }
}