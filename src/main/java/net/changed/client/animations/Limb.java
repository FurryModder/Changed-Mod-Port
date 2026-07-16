package net.changed.client.animations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.changed.client.renderer.model.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Root level limb used to map ModelParts from the vanilla player model to a transfur model
 */
public enum Limb implements StringRepresentable, IExtensibleEnum {
    HEAD("head", HumanoidModel::getHead),
    HEAD2("head2", HumanoidModel::getHead, false),
    HEAD3("head3", HumanoidModel::getHead, false),

    TORSO("torso", model -> model.body),

    LEFT_ARM("left_arm", model -> model.leftArm),
    RIGHT_ARM("right_arm", model -> model.rightArm),

    LEFT_ARM2("left_arm2", model -> model.leftArm, false),
    RIGHT_ARM2("right_arm2", model -> model.rightArm, false),

    LEFT_ARM3("left_arm3", model -> model.leftArm, false),
    RIGHT_ARM3("right_arm3", model -> model.rightArm, false),

    LEFT_LEG("left_leg", model -> model.leftLeg),
    RIGHT_LEG("right_leg", model -> model.rightLeg),

    ABDOMEN("abdomen", model -> model.body, false),

    LOWER_TORSO("lower_torso", model -> model.body, false);

    public static final Codec<Limb> CODEC = Codec.STRING.comapFlatMap(Limb::fromSerial, Limb::getSerializedName);

    @Override
    public @NotNull String getSerializedName() {
        return serialName;
    }

    public static DataResult<Limb> fromSerial(String name) {
        return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid Limb"));
    }

    private final String serialName;
    private final Function<HumanoidModel<?>, ModelPart> getModelPartFn;
    private final Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn;
    private final boolean isVanillaPart;

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn) {
        this.serialName = serialName;
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = model -> model.getLimb(this);
        this.isVanillaPart = true;
    }

    public static Limb create(String enumName, String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn) {
        throw new IllegalStateException("enum not extended");
    }

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, boolean isVanillaPart) {
        this.serialName = serialName;
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = model -> model.getLimb(this);
        this.isVanillaPart = isVanillaPart;
    }

    public static Limb create(String enumName, String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, boolean isVanillaPart) {
        throw new IllegalStateException("enum not extended");
    }

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        this.serialName = serialName;
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = true;
    }

    public static Limb create(String enumName, String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn) {
        throw new IllegalStateException("enum not extended");
    }

    Limb(String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn, boolean isVanillaPart) {
        this.serialName = serialName;
        this.getModelPartFn = getModelPartFn;
        this.getLatexModelPartFn = getLatexModelPartFn;
        this.isVanillaPart = isVanillaPart;
    }

    public static Limb create(String enumName, String serialName, Function<HumanoidModel<?>, ModelPart> getModelPartFn, Function<AdvancedHumanoidModel<?>, ModelPart> getLatexModelPartFn, boolean isVanillaPart) {
        throw new IllegalStateException("enum not extended");
    }

    public ModelPart getModelPart(HumanoidModel<?> model) {
        return getModelPartFn.apply(model);
    }

    public Optional<ModelPart> getModelPartSafe(HumanoidModel<?> model) {
        return Optional.ofNullable(getModelPartFn.apply(model));
    }

    public ModelPart getModelPart(AdvancedHumanoidModel<?> model) {
        return getLatexModelPartFn.apply(model);
    }

    public Optional<ModelPart> getModelPartSafe(AdvancedHumanoidModel<?> model) {
        return Optional.ofNullable(getLatexModelPartFn.apply(model));
    }

    public ModelPart getModelPart(EntityModel<?> model) {
        if (model instanceof HumanoidModel<?> humanoidModel)
            return getModelPart(humanoidModel);
        else if (model instanceof AdvancedHumanoidModel<?> advancedHumanoidModel)
            return getModelPart(advancedHumanoidModel);
        return null;
    }

    public Optional<ModelPart> getModelPartSafe(EntityModel<?> model) {
        return Optional.ofNullable(getModelPart(model));
    }

    public boolean isVanillaPart() {
        return isVanillaPart;
    }
}
