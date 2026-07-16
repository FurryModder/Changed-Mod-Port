package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.init.ChangedRegistry;

import java.util.List;

public class AllCondition extends AbstractCondition {
    public final List<AbstractCondition> components;

    public static final Codec<AllCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(AbstractCondition.CONDITION_CODEC).fieldOf("components").forGetter(condition -> condition.components)
    ).apply(instance, AllCondition::new));

    public AllCondition(List<AbstractCondition> components) {
        this.components = components;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        return components.stream().allMatch(condition -> condition.test(entity));
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
