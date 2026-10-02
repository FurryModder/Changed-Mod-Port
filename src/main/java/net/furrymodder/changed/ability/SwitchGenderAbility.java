package net.furrymodder.changed.ability;

import net.furrymodder.changed.entity.Gender;
import net.furrymodder.changed.entity.GenderedEntity;
import net.furrymodder.changed.init.ChangedRegistry;
import net.furrymodder.changed.init.ChangedSounds;
import net.furrymodder.changed.init.ChangedTransfurVariants;
import net.furrymodder.changed.process.ProcessTransfur;
import net.furrymodder.changed.util.EntityUtil;
import net.minecraft.world.entity.player.Player;

public class SwitchGenderAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getChangedEntity() instanceof GenderedEntity && entity.getEntity() instanceof Player;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).ifPresent(opposite -> {
            entity.replaceVariant(opposite);
            ChangedSounds.broadcastSound(entity.getEntity(), opposite.sound, 1, 1);
        });
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 60;
    }
}
