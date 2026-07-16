package net.changed.ability;

import net.changed.Changed;
import net.changed.entity.ChangedEntity;
import net.changed.entity.variant.TransfurVariant;
import net.changed.init.ChangedEffects;
import net.changed.init.ChangedSounds;
import net.changed.init.ChangedTags;
import net.changed.util.CameraUtil;
import net.changed.util.EntityUtil;
import net.changed.util.LevelUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

public class SirenSingAbilityInstance extends AbstractAbilityInstance {
    private int lastSingTick = 0;

    public SirenSingAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return this.entity.getEntity().isEyeInFluidType(NeoForgeMod.EMPTY_TYPE.value());
    }

    @Override
    public boolean canKeepUsing() {
        return lastSingTick >= entity.getEntity().tickCount && this.canUse();
    }

    @Override
    public void startUsing() {
        final var self = entity.getEntity();
        self.playSound(ChangedSounds.SIREN_SING.get(), 1, 1);
        lastSingTick = entity.getEntity().tickCount + (8 * 20) + 10;
    }

    public void applyConfusionEffect(@Nullable LivingEntity target, float scale) {
        if (scale <= 0f)
            return;
        if (target == null)
            return;
        if (target instanceof Player && !Changed.config.server.playerControllingAbilities.get())
            return;

        int seed = target.getId() * 131313 + ((target.getId() + target.tickCount) / 30);
        RandomSource random = RandomSource.create(seed);
        random.nextInt();
        random.nextInt();
        float value = random.nextFloat();
        if (value < 0.5f * scale) {
             if (!target.hasEffect(ChangedEffects.CONFUSION))
                target.addEffect(new MobEffectInstance(ChangedEffects.CONFUSION, 30));
        }
    }

    protected float computeEffectScale(@NotNull LivingEntity target) {
        if (target instanceof Player && !Changed.config.server.playerControllingAbilities.get())
            return 0f;

        float effectScale = 1f;

        final var level = this.entity.getLevel();
        boolean worldOcclusion = LevelUtil.getBlocksInLine(EntityUtil.getEyeBlock(this.entity.getEntity()), EntityUtil.getEyeBlock(target), 0.25f)
                .anyMatch(blockPos -> {
                    return level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos);
                });

        if (worldOcclusion)
            effectScale *= 0.25f;

        effectScale *= Mth.map(this.entity.getEntity().distanceTo(target), 0, 24, 1.0f, 0.6f);

        return effectScale;
    }

    protected boolean shouldAffectEntity(@NotNull LivingEntity livingEntity) {
        if (TransfurVariant.getEntityVariant(livingEntity) != null)
            return false;

        return livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS);
    }

    @Override
    public void tick() {
        var self = entity.getEntity();
        var level = entity.getLevel();

        self.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false, false));

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                new AABB(self.blockPosition()).inflate(16)).forEach(livingEntity -> {
            if (!this.shouldAffectEntity(livingEntity))
                return;

            float scale = this.computeEffectScale(livingEntity);
            if (scale <= 0)
                return;

            this.applyConfusionEffect(livingEntity, scale);
        });
    }

    @Override
    public void stopUsing() {

    }
}
