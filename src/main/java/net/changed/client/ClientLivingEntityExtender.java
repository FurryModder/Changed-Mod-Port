package net.changed.client;

import net.changed.client.animations.AnimationContainer;
import net.changed.entity.animation.AnimationCategory;
import net.changed.client.animations.AnimationDefinition;
import net.changed.client.animations.AnimationInstance;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ClientLivingEntityExtender {
    Optional<AnimationContainer> getAnimations();

    AnimationContainer getAnimationsOrCreate(Supplier<AnimationContainer> factory);
}
