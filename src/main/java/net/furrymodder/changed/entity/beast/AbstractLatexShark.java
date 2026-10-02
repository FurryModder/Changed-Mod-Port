package net.furrymodder.changed.entity.beast;

import net.furrymodder.changed.entity.AttributePresets;
import net.furrymodder.changed.entity.LatexTypeOld;
import net.furrymodder.changed.entity.TransfurCause;
import net.furrymodder.changed.entity.latex.LatexType;
import net.furrymodder.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public abstract class AbstractLatexShark extends AbstractAquaticEntity {
    public AbstractLatexShark(EntityType<? extends AbstractLatexShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.sharkLike(attributes);
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.GRAY;
    }
}
