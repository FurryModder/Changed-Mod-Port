package net.changed.entity.beast;

import net.changed.entity.AttributePresets;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurMode;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedTransfurVariants;
import net.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class DarkLatexYufeng extends AbstractDarkLatexEntity {
    public DarkLatexYufeng(EntityType<? extends DarkLatexYufeng> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.fromInt(0x3d3d3d);
    }

    @Override
    protected TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.DARK_LATEX_DOUBLE_YUFENG.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }
}
