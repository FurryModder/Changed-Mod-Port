package net.changed.effect;

import net.changed.entity.LivingEntityDataExtension;
import net.changed.util.EntityUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Shock extends MobEffect {
    public Shock() {
        super(MobEffectCategory.HARMFUL, 0x88dfff);
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.shock";
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        EntityUtil.setNoControlTicks(livingEntity, 2);
        return true;
    }
}
