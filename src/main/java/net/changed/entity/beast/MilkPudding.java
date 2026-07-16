package net.changed.entity.beast;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.entity.ChangedEntity;
import net.changed.entity.TransfurCause;
import net.changed.entity.ai.LatexAssimilationDecision;
import net.changed.entity.TransfurMode;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedTransfurVariants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class MilkPudding extends ChangedEntity {
    public MilkPudding(EntityType<? extends MilkPudding> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(8.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(12.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.9);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    protected TransfurVariant<?> getTransfurVariant(LatexAssimilationDecision.Method method) {
        return ChangedTransfurVariants.Gendered.WHITE_LATEX_WOLVES.getRandomVariant(random);
    }

    @Override
    public LatexAssimilationDecision<?> makeLatexAssimilationDecision(TransfurCause cause, LivingEntity targetEntity) {
        IAbstractChangedEntity self = IAbstractChangedEntity.forEntity(this);

        return LatexAssimilationDecision.weak(LatexAssimilationDecision.Method.ABSORPTION,
                ChangedTransfurVariants.Gendered.WHITE_LATEX_WOLVES.getRandomVariant(random),
                self.absorb(),
                this.computeTransfurDamage());
    }

    @Override
    public TransfurVariant<?> getSelfVariant() {
        return null;
    }
}
