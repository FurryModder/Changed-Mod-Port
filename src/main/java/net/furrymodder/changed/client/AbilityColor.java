package net.furrymodder.changed.client;

import net.furrymodder.changed.ability.AbstractAbilityInstance;

import java.util.Optional;

public interface AbilityColor {
    Optional<Integer> getColor(AbstractAbilityInstance abilityInstance, int layer);
}
