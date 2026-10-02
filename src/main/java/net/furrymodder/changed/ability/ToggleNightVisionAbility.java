package net.furrymodder.changed.ability;

import net.furrymodder.changed.entity.Gender;
import net.furrymodder.changed.entity.GenderedEntity;
import net.furrymodder.changed.entity.VisionType;
import net.furrymodder.changed.init.ChangedRegistry;
import net.furrymodder.changed.init.ChangedSounds;
import net.furrymodder.changed.process.ProcessTransfur;
import net.furrymodder.changed.util.EntityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;

public class ToggleNightVisionAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity.getEntity()), (player, variant) -> {
            variant.visionType = variant.visionType == VisionType.NORMAL ? VisionType.NIGHT_VISION : VisionType.NORMAL;
        });
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 60;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.toggle_night_vision.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
