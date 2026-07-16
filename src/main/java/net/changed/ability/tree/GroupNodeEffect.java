package net.changed.ability.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.changed.ability.tree.condition.AbstractCondition;
import net.changed.ability.tree.condition.TrueCondition;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.changed.compat.ForgeRegistries;

import java.util.List;

public class GroupNodeEffect extends AbilityTree.NodeEffect {
    public static final Codec<GroupNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            Codec.list(AbilityTree.NodeEffect.EFFECT_CODEC).fieldOf("effects").forGetter(node -> node.effects)
    ).apply(builder, GroupNodeEffect::new));

    public final AbstractCondition condition;
    public final List<AbilityTree.NodeEffect> effects;

    public GroupNodeEffect(AbstractCondition condition, List<AbilityTree.NodeEffect> effects) {
        this.condition = condition;
        this.effects = effects;
    }

    @Override
    public void applyEffect(AbilityCounter counter) {
        if (condition.test(counter.entity))
            effects.forEach(effect -> effect.applyEffect(counter));
    }

    @Override
    public Codec<? extends AbilityTree.NodeEffect> getCodec() {
        return CODEC;
    }
}
