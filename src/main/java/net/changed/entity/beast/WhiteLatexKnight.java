package net.changed.entity.beast;

import net.changed.entity.AttributePresets;
import net.changed.entity.TransfurMode;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedTransfurVariants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class WhiteLatexKnight extends AbstractLatexWolf {
    public WhiteLatexKnight(EntityType<? extends WhiteLatexKnight> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    protected TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.WHITE_LATEX_CENTAUR.get();
    }
}