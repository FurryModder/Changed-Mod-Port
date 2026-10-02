package net.furrymodder.changed.entity.beast;

import net.furrymodder.changed.entity.AttributePresets;
import net.furrymodder.changed.entity.TransfurMode;
import net.furrymodder.changed.entity.variant.TransfurVariant;
import net.furrymodder.changed.init.ChangedTransfurVariants;
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