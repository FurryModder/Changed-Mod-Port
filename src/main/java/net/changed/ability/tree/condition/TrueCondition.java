package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import net.changed.ability.IAbstractChangedEntity;

import java.util.List;

public class TrueCondition extends AbstractCondition {
    public static final TrueCondition INSTANCE = new TrueCondition();

    public static final Codec<TrueCondition> CODEC = Codec.unit(INSTANCE);

    private TrueCondition() {}

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
