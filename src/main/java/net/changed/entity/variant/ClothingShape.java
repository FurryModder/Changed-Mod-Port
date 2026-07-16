package net.changed.entity.variant;

import net.changed.Changed;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ClothingShape {
    public enum Head implements IExtensibleEnum, StringRepresentable {
        NONE("none", null, false),
        ANTHRO("anthro", null, false);

        public static final Head DEFAULT = ANTHRO;

        private final String serialName;
        private final BiFunction<ArmorTrim, Holder<ArmorMaterial>, ResourceLocation> trimTexture;

        Head(String serialName, String trimPrefix, boolean innerArmorModel) {
            this.serialName = serialName;
            this.trimTexture = trimPrefix == null ? null : Util.memoize((trim, material) -> {
                return (innerArmorModel ? trim.innerTexture(material) : trim.outerTexture(material))
                        .withPath(original -> original.replace("armor", trimPrefix));
            });
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == ANTHRO)
                return null;
            return Changed.modResource("item/empty_armor_slot_" + serialName + "_head");
        }

        @Nullable
        public ResourceLocation getTrimTexture(ArmorTrim trim, Holder<ArmorMaterial> material) {
            if (trimTexture == null)
                return null;

            return trimTexture.apply(trim, material);
        }

        public static Head create(String name, String serialName, String trimPrefix, boolean innerArmorModel) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Torso implements IExtensibleEnum, StringRepresentable {
        NONE("none", null, false),
        ANTHRO("anthro", null, false);

        public static final Torso DEFAULT = ANTHRO;

        private final String serialName;
        private final BiFunction<ArmorTrim, Holder<ArmorMaterial>, ResourceLocation> trimTexture;

        Torso(String serialName, String trimPrefix, boolean innerArmorModel) {
            this.serialName = serialName;
            this.trimTexture = trimPrefix == null ? null : Util.memoize((trim, material) -> {
                return (innerArmorModel ? trim.innerTexture(material) : trim.outerTexture(material))
                        .withPath(original -> original.replace("armor", trimPrefix));
            });
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == ANTHRO)
                return null;
            return Changed.modResource("item/empty_armor_slot_" + serialName + "_torso");
        }

        @Nullable
        public ResourceLocation getTrimTexture(ArmorTrim trim, Holder<ArmorMaterial> material) {
            if (trimTexture == null)
                return null;

            return trimTexture.apply(trim, material);
        }

        public static Torso create(String name, String serialName, String trimPrefix, boolean innerArmorModel) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Legs implements IExtensibleEnum, StringRepresentable {
        NONE("none", null, true),
        BIPEDAL("bipedal", null, true),
        QUADRUPEDAL("quadrupedal", "quadrupedal_armor", true),
        TAIL("tail", "abdomen_armor", false);

        public static final Legs DEFAULT = BIPEDAL;

        private final String serialName;
        private final BiFunction<ArmorTrim, Holder<ArmorMaterial>, ResourceLocation> trimTexture;

        Legs(String serialName, String trimPrefix, boolean innerArmorModel) {
            this.serialName = serialName;
            this.trimTexture = trimPrefix == null ? null : Util.memoize((trim, material) -> {
                return (innerArmorModel ? trim.innerTexture(material) : trim.outerTexture(material))
                        .withPath(original -> original.replace("armor", trimPrefix));
            });
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == BIPEDAL)
                return null;
            return Changed.modResource("item/empty_armor_slot_" + serialName + "_legs");
        }

        @Nullable
        public ResourceLocation getTrimTexture(ArmorTrim trim, Holder<ArmorMaterial> material) {
            if (trimTexture == null)
                return null;

            return trimTexture.apply(trim, material);
        }

        public static Legs create(String name, String serialName, String trimPrefix, boolean innerArmorModel) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Feet implements IExtensibleEnum, StringRepresentable {
        NONE("none", null, false),
        BIPEDAL("bipedal", null, false),
        QUADRUPEDAL("quadrupedal", "quadrupedal_armor", false),
        TAIL("tail", "abdomen_armor", true);

        public static final Feet DEFAULT = BIPEDAL;

        private final String serialName;
        private final BiFunction<ArmorTrim, Holder<ArmorMaterial>, ResourceLocation> trimTexture;

        Feet(String serialName, String trimPrefix, boolean innerArmorModel) {
            this.serialName = serialName;
            this.trimTexture = trimPrefix == null ? null : Util.memoize((trim, material) -> {
                return (innerArmorModel ? trim.innerTexture(material) : trim.outerTexture(material))
                        .withPath(original -> original.replace("armor", trimPrefix));
            });
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == BIPEDAL)
                return null;
            return Changed.modResource("item/empty_armor_slot_" + serialName + "_feet");
        }

        @Nullable
        public ResourceLocation getTrimTexture(ArmorTrim trim, Holder<ArmorMaterial> material) {
            if (trimTexture == null)
                return null;

            return trimTexture.apply(trim, material);
        }

        public static Feet create(String name, String serialName, String trimPrefix, boolean innerArmorModel) {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
