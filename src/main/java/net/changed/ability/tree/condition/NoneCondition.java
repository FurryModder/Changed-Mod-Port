package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.ability.IAbstractChangedEntity;

import java.util.List;

public class NoneCondition extends AbstractCondition {
    public final List<AbstractCondition> components;

    public static final Codec<NoneCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(AbstractCondition.CONDITION_CODEC).fieldOf("components").forGetter(condition -> condition.components)
    ).apply(instance, NoneCondition::new));

    public NoneCondition(List<AbstractCondition> components) {
        this.components = components;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        return components.stream().noneMatch(condition -> condition.test(entity));
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
