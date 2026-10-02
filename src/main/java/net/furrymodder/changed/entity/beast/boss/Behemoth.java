package net.furrymodder.changed.entity.beast.boss;

import net.furrymodder.changed.entity.ChangedEntity;
import net.furrymodder.changed.entity.TransfurMode;
import net.furrymodder.changed.entity.latex.LatexType;
import net.furrymodder.changed.entity.variant.TransfurVariant;
import net.furrymodder.changed.init.ChangedLatexTypes;
import net.furrymodder.changed.init.ChangedTransfurVariants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class Behemoth extends ChangedEntity {
    public Behemoth(EntityType<? extends Behemoth> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.xpReward = 40;
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.WHITE_LATEX.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public TransfurVariant<?> getSelfVariant() {
        return null;
    }

    protected TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.WHITE_LATEX_WOLF_MALE.get();
    }

    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }
}