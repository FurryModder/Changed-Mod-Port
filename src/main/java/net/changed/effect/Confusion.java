package net.changed.effect;

import net.changed.util.EntityUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Confusion extends MobEffect {
    public Confusion() {
        super(MobEffectCategory.HARMFUL, 0xb688ff);
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.confusion";
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        EntityUtil.setInvertControlTicks(livingEntity, 2);
        return true;
    }
}
