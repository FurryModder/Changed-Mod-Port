package net.changed.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.entity.ChangedEntity;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class TameLatexTrigger extends SimpleCriterionTrigger<TameLatexTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ChangedEntity entity) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(lootcontext));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)
        ).apply(instance, TriggerInstance::new));

        public static TriggerInstance tamedAnimal() {
            return new TriggerInstance(Optional.empty(), Optional.empty());
        }

        public static TriggerInstance tamedAnimal(EntityPredicate entityPredicate) {
            return new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entityPredicate)));
        }

        public boolean matches(LootContext lootContext) {
            return this.entity.isEmpty() || this.entity.get().matches(lootContext);
        }
    }
}
