package net.changed.advancements.critereon;

import com.mojang.serialization.Codec;
import net.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class TransfurTrigger extends SimpleCriterionTrigger<TransfurTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, TransfurVariantInstance<?> form) {
        this.trigger(player, predicate -> predicate.matches(form));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, TransfurPredicate form) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = TransfurPredicate.CODEC.xmap(
                predicate -> new TriggerInstance(Optional.empty(), predicate),
                TriggerInstance::form
        );

        public static TriggerInstance transfurred() {
            return new TriggerInstance(Optional.empty(), TransfurPredicate.ANY);
        }

        public static TriggerInstance transfurred(TransfurPredicate predicate) {
            return new TriggerInstance(Optional.empty(), predicate);
        }

        public boolean matches(TransfurVariantInstance<?> form) {
            return this.form.matches(form);
        }
    }
}
