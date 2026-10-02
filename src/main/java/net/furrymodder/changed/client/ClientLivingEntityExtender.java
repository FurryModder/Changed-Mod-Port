package net.furrymodder.changed.client;

import net.furrymodder.changed.client.animations.AnimationContainer;
import net.furrymodder.changed.entity.animation.AnimationCategory;
import net.furrymodder.changed.client.animations.AnimationDefinition;
import net.furrymodder.changed.client.animations.AnimationInstance;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ClientLivingEntityExtender {
    Optional<AnimationContainer> getAnimations();

    AnimationContainer getAnimationsOrCreate(Supplier<AnimationContainer> factory);
}
