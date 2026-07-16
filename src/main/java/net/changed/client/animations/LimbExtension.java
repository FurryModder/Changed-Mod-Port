package net.changed.client.animations;

import net.changed.client.renderer.animate.HumanoidAnimator;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Only available for AdvancedHumanoidModels
 */
public abstract class LimbExtension {
    public interface ModelPartFetcher {
        @Nullable ModelPart getModelPart(@Nullable HumanoidAnimator<?, ?> animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param);
    }

    public interface ModelPartFetcherWithStage<T extends HumanoidAnimator.Animator<?, ?>> {
        @Nullable ModelPart getModelPart(T animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param);
    }

    public abstract boolean acceptsLimb(@Nullable Limb limb);
    public abstract @Nullable ModelPart getModelPart(@Nullable HumanoidAnimator<?, ?> animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param);

    public Optional<ModelPart> getModelPartSafe(HumanoidAnimator<?, ?> animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param) {
        return Optional.ofNullable(this.getModelPart(animator, limb, limbRoot, param));
    }

    public static LimbExtension simple(Set<Limb> parents, String childName) {
        return forFetcher(parents, (animator, limb, limbRoot, param) -> {
            return limbRoot == null || !limbRoot.children.containsKey(childName) ? null : limbRoot.getChild(childName);
        });
    }

    public static LimbExtension forFetcher(Set<Limb> parents, ModelPartFetcher fetcher) {
        return new LimbExtension() {
            @Override
            public boolean acceptsLimb(@Nullable Limb limb) {
                return parents.contains(limb);
            }

            @Override
            public @Nullable ModelPart getModelPart(@Nullable HumanoidAnimator<?, ?> animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param) {
                return fetcher.getModelPart(animator, limb, limbRoot, param);
            }
        };
    }

    public static <T extends HumanoidAnimator.Animator<?, ?>> LimbExtension forAnimator(Set<Limb> parents, Class<? super T> animatorBaseClass, ModelPartFetcherWithStage<T> fetcher) {
        return new LimbExtension() {
            @Override
            public boolean acceptsLimb(@Nullable Limb limb) {
                return parents.contains(limb);
            }

            @Override
            public @Nullable ModelPart getModelPart(@Nullable HumanoidAnimator<?, ?> animator, @NotNull Limb limb, @Nullable ModelPart limbRoot, String param) {
                if (animator == null) return null;
                return animator.getAnimators(HumanoidAnimator.AnimateStage.INIT)
                        .filter(subAnimator -> animatorBaseClass.isAssignableFrom(subAnimator.getClass()))
                        .findFirst().map(subAnimator -> fetcher.getModelPart((T) subAnimator, limb, limbRoot, param))
                        .orElse(null);
            }
        };
    }
}
