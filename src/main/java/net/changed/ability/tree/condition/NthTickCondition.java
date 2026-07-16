package net.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.ability.IAbstractChangedEntity;
import net.changed.data.RegistryElementPredicate;
import net.minecraft.world.level.block.Block;
import net.changed.compat.ForgeRegistries;

import java.util.List;

public class NthTickCondition extends AbstractCondition {
    public final int tickRate;

    public static final Codec<NthTickCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tickRate").forGetter(condition -> condition.tickRate)
    ).apply(instance, NthTickCondition::new));

    public NthTickCondition(int tickRate) {
        this.tickRate = tickRate;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        return entity.getEntity().tickCount % tickRate == 0;
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
