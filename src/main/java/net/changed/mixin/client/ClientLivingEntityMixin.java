package net.changed.mixin.client;

import net.changed.client.ClientLivingEntityExtender;
import net.changed.client.animations.AnimationContainer;
import net.changed.entity.animation.AnimationCategory;
import net.changed.client.animations.AnimationDefinition;
import net.changed.client.animations.AnimationInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This mixin only loads on the client, so we can safely control animation instances here
 */
@Mixin(LivingEntity.class)
public class ClientLivingEntityMixin implements ClientLivingEntityExtender {
    @Unique
    private AnimationContainer animationContainer = null;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAnimations(CallbackInfo ci) {
        if (animationContainer != null) {
            animationContainer.tick();
            if (animationContainer.isEmpty())
                animationContainer = null;
        }
    }

    @Override
    public Optional<AnimationContainer> getAnimations() {
        return Optional.ofNullable(animationContainer);
    }

    @Override
    public AnimationContainer getAnimationsOrCreate(Supplier<AnimationContainer> factory) {
        if (animationContainer != null)
            return animationContainer;
        animationContainer = factory.get();
        return animationContainer;
    }
}
