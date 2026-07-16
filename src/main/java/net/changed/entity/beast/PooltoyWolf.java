package net.changed.entity.beast;

import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class PooltoyWolf extends AbstractPooltoy {
    public static final float SCALE = 1.3F;

    public PooltoyWolf(EntityType<? extends PooltoyWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.95);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(4.0);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.fromInt(0x50c3ff);
    }
}
