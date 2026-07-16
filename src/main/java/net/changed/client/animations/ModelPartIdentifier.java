package net.changed.client.animations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.changed.client.renderer.animate.HumanoidAnimator;
import net.changed.client.renderer.model.AdvancedHumanoidModel;
import net.changed.entity.ChangedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public record ModelPartIdentifier(@Nullable Limb limb, @Nullable LimbExtension extension, @Nullable String param) implements StringRepresentable {
    private static final String MATCH_ANY = "any";

    // Just the limb (matches arms, head, torso, etc.)
    public static ModelPartIdentifier forLimb(Limb limb) {
        return new ModelPartIdentifier(limb, null, null);
    }

    // Just the extension (matches any limb that has the extension)
    public static ModelPartIdentifier forExtension(LimbExtension extension) {
        return new ModelPartIdentifier(null, extension, null);
    }

    // Limb and extension (matches one limb that has the extension)
    public static ModelPartIdentifier forLimbAndExtension(Limb limb, LimbExtension extension) {
        return new ModelPartIdentifier(limb, extension, null);
    }

    // Just the extension (matches any limb that has the extension)
    public static ModelPartIdentifier forExtension(LimbExtension extension, @Nullable String param) {
        return new ModelPartIdentifier(null, extension, param);
    }

    // Limb and extension (matches one limb that has the extension)
    public static ModelPartIdentifier forLimbAndExtension(Limb limb, LimbExtension extension, @Nullable String param) {
        return new ModelPartIdentifier(limb, extension, param);
    }

    public boolean isVanillaPart() {
        return extension == null && limb != null && limb.isVanillaPart();
    }

    public @Nullable ModelPart getModelPart(HumanoidModel<?> model) {
        if (extension == null && limb != null && limb.isVanillaPart())
            return limb.getModelPart(model);
        return null;
    }

    public Optional<ModelPart> getModelPartSafe(HumanoidModel<?> model) {
        return Optional.ofNullable(this.getModelPart(model));
    }

    private @Nullable HumanoidAnimator<?, ?> getAnimatorOrNull(AdvancedHumanoidModel<?> model, ChangedEntity entity) {
        return ((AdvancedHumanoidModel)model).getAnimator(entity);
    }

    public @Nullable ModelPart getModelPart(AdvancedHumanoidModel<?> model, ChangedEntity entity) {
        if (extension == null && limb != null)
            return limb.getModelPart(model);
        else if (extension != null && limb != null) {
            return extension.acceptsLimb(limb) ? extension.getModelPart(getAnimatorOrNull(model, entity), limb, limb.getModelPart(model), param) : null;
        } else if (extension != null) {
            final var animator = getAnimatorOrNull(model, entity);
            return Arrays.stream(Limb.values()).filter(extension::acceptsLimb).map(limb -> {
                return extension.getModelPartSafe(animator, limb, limb.getModelPart(model), param);
            }).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(null);
        }

        return null;
    }

    public Optional<ModelPart> getModelPartSafe(AdvancedHumanoidModel<?> model, ChangedEntity entity) {
        return Optional.ofNullable(this.getModelPart(model, entity));
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public @NotNull String getSerializedName() {
        if (extension != null) {
            if (param != null)
                return (limb == null ? MATCH_ANY : limb.getSerializedName()) + "/" + LimbExtensions.toSerial(extension) + "#" + param;
            else
                return (limb == null ? MATCH_ANY : limb.getSerializedName()) + "/" + LimbExtensions.toSerial(extension);
        }
        else
            return Objects.requireNonNull(limb).getSerializedName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModelPartIdentifier other)
            return Objects.equals(limb, other.limb) && Objects.equals(extension, other.extension) && (extension == null || Objects.equals(param, other.param));
        return false;
    }

    public static final Codec<ModelPartIdentifier> CODEC = Codec.STRING.comapFlatMap(string -> {
        if (string.contains("/")) {
            int slashIndx = string.indexOf('/');
            int tagIndx = string.indexOf('#');
            String param = tagIndx == -1 ? null : string.substring(tagIndx + 1);
            final String limbName = string.substring(0, slashIndx);
            final String extensionName = tagIndx == -1 ? string.substring(slashIndx + 1) : string.substring(slashIndx + 1, tagIndx);
            return ResourceLocation.read(extensionName)
                    .flatMap(LimbExtensions::fromSerial)
                    .flatMap(extension -> {
                        if (MATCH_ANY.equals(limbName))
                            return DataResult.success(ModelPartIdentifier.forExtension(extension, param));
                        else
                            return Limb.fromSerial(limbName).map(limb -> ModelPartIdentifier.forLimbAndExtension(limb, extension, param));
                    });
        } else {
            return Limb.fromSerial(string).map(ModelPartIdentifier::forLimb);
        }
    }, ModelPartIdentifier::getSerializedName);

    public boolean hasExtension() {
        return extension != null;
    }
}
