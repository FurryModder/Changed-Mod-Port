package net.changed.entity.ai;

import net.changed.Changed;
import net.changed.entity.beast.AbstractDarkLatexEntity;
import net.changed.entity.variant.TransfurVariantInstance;
import net.changed.init.ChangedSounds;
import net.changed.network.packet.GrabEntityPacket;
import net.changed.process.ProcessTransfur;
import net.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.changed.network.PacketDistributor;

public class DarkLatexSuitOwnerGoal extends MeleeAttackGoal {
    protected final AbstractDarkLatexEntity entity;

    public DarkLatexSuitOwnerGoal(AbstractDarkLatexEntity entity, double speedModifier, boolean visualPersistence) {
        super(entity, speedModifier, visualPersistence);

        this.entity = entity;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        var ability = entity.getGrabAbility();
        if (ability == null) {
            entity.setTarget(null);
            entity.setFavor(DarkLatexFavor.NONE);
            return;
        }

        if (target == entity.getOwner()) {
            if (this.canPerformAttack(target)) {
                this.resetAttackCooldown();

                if (ability.suitEntity(target)) {
                    ability.grabbedHasControl = true;
                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                            new GrabEntityPacket(entity, target, GrabEntityPacket.GrabType.SUIT));
                    ChangedSounds.broadcastSound(entity, ChangedSounds.LATEX_SUIT_ENTITY, 1.0f, 1.0f);
                }
            }
        } else {
            // Re-evaluate nearby entities
            entity.setTarget(null);
        }
    }

    @Override
    public boolean canUse() {
        if (this.entity.getCurrentFavor() != DarkLatexFavor.SUIT_OWNER)
            return false;
        var owner = this.entity.getOwner();
        if (owner == null)
            return false;

        var ability = entity.getGrabAbility();
        if (ability == null || ability.grabbedEntity == owner)
            return false;

        if (ProcessTransfur.isPlayerTransfurred(EntityUtil.playerOrNull(owner)))
            return false;

        this.entity.setTarget(owner);
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getCurrentFavor() != DarkLatexFavor.SUIT_OWNER)
            return false;
        var owner = this.entity.getOwner();
        if (owner == null)
            return false;

        var ability = entity.getGrabAbility();
        if (ability == null || ability.grabbedEntity == owner)
            return false;

        if (ProcessTransfur.isPlayerTransfurred(EntityUtil.playerOrNull(owner)))
            return false;

        this.entity.setTarget(owner);
        return super.canContinueToUse();
    }
}
