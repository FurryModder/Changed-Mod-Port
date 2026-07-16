package net.changed.tutorial;

import net.changed.ability.AbstractAbilityInstance;
import net.minecraft.client.tutorial.TutorialStepInstance;

public interface ChangedTutorialInstance extends TutorialStepInstance {
    default void onOpenRadial() {

    }

    default void onUseAbility(AbstractAbilityInstance abilityInstance) {

    }
}
