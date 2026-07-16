package net.changed.entity.beast;

import net.changed.entity.*;
import net.changed.entity.latex.LatexType;
import net.changed.init.ChangedAttributes;
import net.changed.util.Color3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class LatexBee extends ChangedEntity {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(LatexBee.class, EntityDataSerializers.BYTE);
    public LatexBee(EntityType<? extends LatexBee> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.05);
        attributes.getInstance(NeoForgeMod.SWIM_SPEED).setBaseValue(0.75);
        attributes.getInstance(ChangedAttributes.AIR_CAPACITY).setBaseValue(7.5);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE).setBaseValue(2.5);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.ABSORPTION; }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#fdbf77");
    }
}
