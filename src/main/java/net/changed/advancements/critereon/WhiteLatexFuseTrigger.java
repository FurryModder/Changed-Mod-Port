package net.changed.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class WhiteLatexFuseTrigger extends SimpleCriterionTrigger<WhiteLatexFuseTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int ticks) {
        this.trigger(player, predicate -> predicate.matches(ticks));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, int ticks) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.INT.optionalFieldOf("ticks", 0).forGetter(TriggerInstance::ticks)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(int ticks) {
            return this.ticks <= ticks;
        }
    }
}
