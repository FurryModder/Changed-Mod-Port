package net.changed.client;

import net.changed.ability.AbstractAbilityInstance;

import java.util.Optional;

public interface AbilityColor {
    Optional<Integer> getColor(AbstractAbilityInstance abilityInstance, int layer);
}
