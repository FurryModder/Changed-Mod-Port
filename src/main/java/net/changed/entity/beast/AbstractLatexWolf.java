package net.changed.entity.beast;

import net.changed.entity.AttributePresets;
import net.changed.entity.ChangedEntity;
import net.changed.entity.LatexTypeOld;
import net.changed.entity.TransfurMode;
import net.changed.entity.latex.LatexType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public abstract class AbstractLatexWolf extends ChangedEntity {
    public AbstractLatexWolf(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.REPLICATION; }

    @Override
    public void addAdditionalSaveData(CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }
}
