package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.init.ChangedRegistry;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractCondition implements Predicate<IAbstractChangedEntity> {
    public static final Codec<AbstractCondition> CONDITION_CODEC = ChangedRegistry.ABILITY_EFFECT_CONDITIONS.get().getCodec().dispatch("type",
            AbstractCondition::getCodec, MapCodec::assumeMapUnsafe);

    public abstract Codec<? extends AbstractCondition> getCodec();
}
