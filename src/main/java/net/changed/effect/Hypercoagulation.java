package net.changed.effect;

import net.changed.init.ChangedDamageSources;
import net.changed.init.ChangedTags;
import net.changed.util.EntityUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Hypercoagulation extends MobEffect {
    public Hypercoagulation() {
        super(MobEffectCategory.HARMFUL, 14688288);
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.hypercoagulation";
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (EntityUtil.maybeGetOverlaying(entity).getType().is(ChangedTags.EntityTypes.LATEX))
            return true;
        entity.hurt(ChangedDamageSources.BLOODLOSS.source(entity.level().registryAccess()), 1.0f);
        return true;
    }
}
