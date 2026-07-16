package net.changed.entity.ai;

import net.changed.ability.IAbstractChangedEntity;
import net.changed.ability.ILatexAssimilatedEntity;
import net.changed.entity.ChangedEntity;
import net.changed.entity.TransfurCause;
import net.changed.entity.TransfurContext;
import net.changed.entity.variant.GenderedPair;
import net.changed.entity.variant.TransfurVariant;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TransfurDecider<T extends LivingEntity> {
    LatexAssimilationDecision<?> apply(T assimilatedMob, LivingEntity target);

    default Function<LivingEntity, LatexAssimilationDecision<?>> withAssimilatedMob(T assimilatedMob) {
        return target -> this.apply(assimilatedMob, target);
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(TransfurVariant<V> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(Supplier<? extends TransfurVariant<V>> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, VM extends ChangedEntity, VF extends ChangedEntity> TransfurDecider<T> simpleMobDecider(GenderedPair<VM, VF> variant, float damage) {
        return simpleMobDecider(variant, damage, (entity, transfurredEntity) -> {});
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(TransfurVariant<V> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        Objects.requireNonNull(variant);
        Objects.requireNonNull(postTransfurListener);

        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant,
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }

    static <T extends LivingEntity, V extends ChangedEntity> TransfurDecider<T> simpleMobDecider(Supplier<? extends TransfurVariant<V>> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        Objects.requireNonNull(variant);
        Objects.requireNonNull(postTransfurListener);

        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant.get(),
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }

    static <T extends LivingEntity, VM extends ChangedEntity, VF extends ChangedEntity> TransfurDecider<T> simpleMobDecider(GenderedPair<VM, VF> variant, float damage, BiConsumer<T, IAbstractChangedEntity> postTransfurListener) {
        Objects.requireNonNull(variant);
        Objects.requireNonNull(postTransfurListener);

        return (assimilatedMob, target) -> {
            ILatexAssimilatedEntity self = ILatexAssimilatedEntity.forEntity(assimilatedMob);

            return LatexAssimilationDecision.weakAbsorption(variant.getRandomVariant(assimilatedMob.getRandom()),
                    self.absorb(), damage,
                    transfurredEntity -> postTransfurListener.accept(assimilatedMob, transfurredEntity));
        };
    }
}
